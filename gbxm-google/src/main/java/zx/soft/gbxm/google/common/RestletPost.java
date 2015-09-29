package zx.soft.gbxm.google.common;

import java.io.IOException;
import java.util.LinkedList;

import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.google.domain.PostData;
import zx.soft.utils.json.JsonUtils;

public class RestletPost {
	private static final ClientResource clientResourceGB = new ClientResource(ConstUtils.URLs[0]);
	private static final ClientResource clientResourceST = new ClientResource(ConstUtils.URLs[1]);

	private static Logger logger = LoggerFactory.getLogger(RestletPost.class);

	//设置ClientResource
	private final LinkedList<ClientResource> setClientResources(){
		LinkedList<ClientResource> result = new LinkedList<>();

		for (String url : ConstUtils.URLs){
			ClientResource resource = new ClientResource(url);
			result.add(resource);
		}
		return result;
	}

	public void post(PostData data){
		final PostData params = data;
		for (int i = 0 ; i < setClientResources().size();i++){
			final ClientResource clientResource = setClientResources().get(i);
			final Integer index = i;
			try{
				new Thread(new Runnable() {
					@Override
					public void run() {
						Representation entity = new StringRepresentation(JsonUtils.toJsonWithoutPretty(params));
						entity.setMediaType(MediaType.APPLICATION_JSON);

						try {
							clientResource.post(entity);
							Response response = clientResource.getResponse();
							try {
								logger.info("post {} solr {}", ConstUtils.URLs[index], response.getEntity().getText());
							} catch (IOException e) {
								e.printStackTrace();
							}
							clientResource.release();
						} catch (ResourceException e) {
							logger.error("post {} data to solr error",ConstUtils.URLs[index]);
							logger.error(e.getMessage());
							e.printStackTrace();
						}
						clientResource.release();
					}
				},"Post"+i).start();
			}catch (Exception e){
				e.printStackTrace();
			}

		}

	}

	public static boolean postGB(PostData data) {
		Representation entity = new StringRepresentation(JsonUtils.toJsonWithoutPretty(data));
		entity.setMediaType(MediaType.APPLICATION_JSON);

//		entity.setEncodings();
		try {
			clientResourceGB.post(entity);
			Response response = clientResourceGB.getResponse();
			try {
				logger.info(response.getEntity().getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			clientResourceGB.release();
			return true;
		} catch (ResourceException e) {
			logger.error("post GBXM data to solr error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		clientResourceGB.release();
		return false;
	}

	public static boolean postST(PostData data){
		Representation entity = new StringRepresentation(JsonUtils.toJsonWithoutPretty(data));
		entity.setMediaType(MediaType.APPLICATION_JSON);

//		entity.setEncodings();
		try {
			clientResourceST.post(entity);
			Response response = clientResourceST.getResponse();
			try {
				logger.info(response.getEntity().getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			clientResourceST.release();
			return true;
		} catch (ResourceException e) {
			logger.error("post ST data to solr error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		clientResourceST.release();
		return false;
	}

	public static boolean postGX(PostData data){
		Representation entity = new StringRepresentation(JsonUtils.toJsonWithoutPretty(data));
		entity.setMediaType(MediaType.APPLICATION_JSON);

//		entity.setEncodings();
		try {
			clientResourceST.post(entity);
			Response response = clientResourceST.getResponse();
			try {
				logger.info(response.getEntity().getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			clientResourceST.release();
			return true;
		} catch (ResourceException e) {
			logger.error("post ST data to solr error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		clientResourceST.release();
		return false;
	}


}
