package zx.soft.gbxm.google.common;

import java.io.IOException;

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
			return true;
		} catch (ResourceException e) {
			logger.error("post GBXM data to solr error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
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
			return true;
		} catch (ResourceException e) {
			logger.error("post ST data to solr error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}
