package zx.soft.gbxm.facebook.common;

import java.io.IOException;

import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.facebook.domain.PostData;
import zx.soft.utils.json.JsonUtils;

public class RestletPost {

	private static final ClientResource clientResource = new ClientResource(ConstUtils.URL);

	private static Logger logger = LoggerFactory.getLogger(RestletPost.class);

	public static boolean post(PostData data) {
		Representation entity = new StringRepresentation(JsonUtils.toJsonWithoutPretty(data));
		entity.setMediaType(MediaType.APPLICATION_JSON);
		try {
			clientResource.post(entity);
			Response response = clientResource.getResponse();
			try {
				logger.info(response.getEntity().getText());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} catch (ResourceException e) {
			logger.error("post data to solr error");
			logger.error(e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
}
