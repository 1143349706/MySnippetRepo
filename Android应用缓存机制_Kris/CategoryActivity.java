package cn.eoe.wiki.activity;

public abstract class CategoryActivity extends SliderActivity{
	
	private static final int	HANDLER_DISPLAY_CATEGORY 	= 0x0001;
	private static final int	HANDLER_LOAD_CATEGORY_ERROR = 0x0002;
	private static final int	HANDLER_LOAD_CATEGORY_DB 	= 0x0003;
	private static final int	HANDLER_LOAD_CATEGORY_NET 	= 0x0004;
	private static final int	HANDLER_REFRESH_CATEGORY_NET= 0x0005;
	private static final int	HANDLER_DISPLAY_RECENT_UPDATED= 0x0006;

	void getCategory(String url) {
		if(TextUtils.isEmpty(url))
			throw new IllegalArgumentException("You must give a not empty url.");
		
		this.mUrl = url;
		mHandler.sendEmptyMessage(HANDLER_LOAD_CATEGORY_DB);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLER_DISPLAY_CATEGORY:
				L.d("HANDLER_DISPLAY_CATEGORY");
				generateCategories((CategoryJson)msg.obj);
				break;
			case HANDLER_LOAD_CATEGORY_ERROR:
				getCategoriesError(getString(R.string.tip_get_category_error));
				break;
			case HANDLER_LOAD_CATEGORY_DB:
				new LoadCategoryFromDb().execute(mUrl);
				break;
			case HANDLER_LOAD_CATEGORY_NET:
				new HttpManager(mUrl,null, HttpManager.GET, getCategoriesTransaction).start();
				break;
			case HANDLER_REFRESH_CATEGORY_NET:
				new HttpManager(mUrl,null, HttpManager.GET, refreshCategoriesTransaction).start();
				break;
			case HANDLER_DISPLAY_RECENT_UPDATED:
				//
				break;
			default:
				break;
			}
		}
	};
	
	class LoadCategoryFromDb extends AsyncTask<String, Integer, Boolean>
	{
		@Override
		protected Boolean doInBackground(String... params) {
			String url = params[0];
			WikiEntity wiki = mWikiDao.getWikiByUrl(url);
			if(wiki!=null) {
				String content = wiki.getWikiFileContent();
				mapperJson(content,false);
				L.d("get the category from the cache");
			}
			else {
				L.d("can not get the content from the db");
				mHandler.sendEmptyMessage(HANDLER_LOAD_CATEGORY_NET);
			}
			return null;
		}
	}
	
	private void mapperJson(String result,boolean fromNet)
	{
		try {
			mResponseObject = mObjectMapper.readValue(result, new TypeReference<CategoryJson>() { });
			L.e("version:"+mResponseObject.getVersion());
			if(fromNet) {
				//if it is load from net ,save category
				saveWikiCategory(mResponseObject.getVersion(),mResponseObject.getPageId(), result);
			}
			else {
				//check the net wiki
				UpdateEntity updateEntity = mWikiUpdateDao.getWikiUpdateByUrl(mUrl);
				if(updateEntity!=null) {
					long current = System.currentTimeMillis();
					if((current-updateEntity.getUpdateDate())>DateUtil.DAY_MILLIS) {
						L.d("need to refreah the cache:"+mUrl);
						//check the new wiki every day
						mHandler.sendEmptyMessage(HANDLER_REFRESH_CATEGORY_NET);
					}
				}
				else {
					//�������Ǵӻ����ж�ȡ�ģ�����ȥû��һ��update��ʱ�������update���ݿ�
					mWikiUpdateDao.addOrUpdateTime(mUrl);
				}
			}
			mHandler.obtainMessage(HANDLER_DISPLAY_CATEGORY, mResponseObject).sendToTarget();
		} catch (Exception e) {
			L.e("getCategory Transaction exception", e);
			if(!fromNet) {
				L.d("category content is erro which is read from the cache dir");
				//������Ǵ��������ġ������˻��ô�����ȥ��һ��
				mHandler.sendEmptyMessage(HANDLER_LOAD_CATEGORY_NET);
			}
			else {
				//����Ǵ������������ģ������˾ʹ�����
				mHandler.obtainMessage(HANDLER_LOAD_CATEGORY_ERROR).sendToTarget();
			}
		}
	}
	
	private boolean saveWikiCategory(int version,String pageid,String result)
	{
		if(!FileUtil.isExternalStorageEnable())//no dscard , return
			return false;
		WikiEntity entity = new WikiEntity();
		entity.setPageId(pageid);
		entity.setUri(mUrl);
		entity.setVersion(version);
		try {
			return mWikiDao.saveOrUpdateWiki(entity,result);
		} catch (Exception e) {
			L.e("save failed",e);
		}
		return false;
	}
	
	abstract void getCategoriesError(String showText);
	abstract void generateCategories(CategoryJson responseObject);

	
	public ITransaction getCategoriesTransaction = new ITransaction() {
		@Override
		public void transactionOver(String result) {
			mapperJson(result,true);
			L.d("get the category from the net");
		}
		
		@Override
		public void transactionException(int erroCode,String result, Exception e) {
			mHandler.obtainMessage(HANDLER_LOAD_CATEGORY_ERROR).sendToTarget();
		}
	};

	public ITransaction refreshCategoriesTransaction = new ITransaction() {
		@Override
		public void transactionOver(String result) {
			L.d("refresh the category from the net");
			try {
				CategoryJson responseObject = mObjectMapper.readValue(result, new TypeReference<CategoryJson>() { });
				//save category
				saveWikiCategory(responseObject.getVersion(),responseObject.getPageId(), result);
			}catch (Exception e) {
				L.e("refresh category error[mapper json]");
			}
		}
		
		@Override
		public void transactionException(int erroCode,String result, Exception e) {
			L.e("Refresh the category exception:"+erroCode);
		}
	};
}
