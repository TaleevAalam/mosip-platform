package io.mosip.registration.processor.packet.manager.idreposervice.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.AbisConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.idrepo.dto.IdResponseDTO;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.packet.manager.idreposervice.IdRepoService;

@RefreshScope
@Service
public class IdRepoServiceImpl implements IdRepoService {

	/** The rest client service. */
	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@SuppressWarnings("unchecked")
	@Override
	public Number getUinFromIDRepo(String machedRegId, String regProcessorDemographicIdentity)
			throws IOException, ApisResourceAccessException {
		List<String> pathSegments = new ArrayList<>();
		pathSegments.add("rid");
		pathSegments.add(machedRegId);
		Number uin = null;

		@SuppressWarnings("unchecked")
		ResponseWrapper<IdResponseDTO> response;

		response = (ResponseWrapper<IdResponseDTO>) restClientService.getApi(ApiName.IDREPOSITORY, pathSegments, "", "",
				ResponseWrapper.class);

		if (response.getResponse() != null) {
			Gson gsonObj = new Gson();
			String jsonString = gsonObj.toJson(response.getResponse());
			JSONObject identityJson = JsonUtil.objectMapperReadValue(jsonString, JSONObject.class);
			JSONObject demographicIdentity = JsonUtil.getJSONObject(identityJson, regProcessorDemographicIdentity);
			uin = JsonUtil.getJSONValue(demographicIdentity, AbisConstant.UIN);
		}
		return uin;
	}

}
