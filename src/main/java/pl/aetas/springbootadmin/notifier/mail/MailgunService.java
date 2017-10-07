package pl.aetas.springbootadmin.notifier.mail;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;

/**
 * @author gelder
 */
@Service
public class MailgunService
{
	private static final Logger logger = LoggerFactory.getLogger(MailgunService.class);

	@Value("${spring.boot.admin.notify.mail.mailgun.api.url}")
	private String baseUrl;
	@Value("${spring.boot.admin.notify.mail.mailgun.api.key}")
	private String apiKey;

	public boolean sendPlainTextMail(String from, String to, String subject, String messageBody)
	{
		logger.info("Sending plain text mail with subject " +subject +" to " +to);

		MultiValueMap<String, Object> formData = createCommonData(from, to, subject);
		formData.add("text", messageBody);

		HttpHeaders headers = createHttpHeadersWithBasicAuth();
		return doSend(headers, formData);
	}

	private MultiValueMap<String, Object> createCommonData(String from, String to, String subject)
	{
		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
		formData.add("from", from);
		formData.add("to", to);
		formData.add("subject", subject);
		return formData;
	}

	private boolean doSend(HttpHeaders headers, MultiValueMap<String, Object> formData)
	{
		RestTemplate restTemplate = new RestTemplate();
		try
		{
			ResponseEntity<String> responseEntity = restTemplate.exchange(baseUrl, HttpMethod.POST, new HttpEntity<>(formData, headers), String.class);
			if(!responseEntity.getStatusCode().is2xxSuccessful())
			{
				logger.error("Failed to send mail. Reason: " +responseEntity);
				return false;
			}
		}
		catch (RestClientException ex)
		{
			logger.error("Failed to send mail. Reason: " +ex.getMessage());
			logger.debug("Details: ", ex);
			return false;
		}
		logger.debug("Sending mail was succesful");
		return true;
	}

	private HttpHeaders createHttpHeadersWithBasicAuth()
	{
		HttpHeaders headers = new HttpHeaders();
		String auth = "api" + ":" + apiKey;
		byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
		String authHeader = "Basic " + new String(encodedAuth);
		headers.set("Authorization", authHeader);
		return headers;
	}
}
