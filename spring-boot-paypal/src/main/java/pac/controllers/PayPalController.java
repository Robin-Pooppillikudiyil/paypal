package pac.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pac.paypal.PayPalClient;

@RestController
@RequestMapping(value = "/paypal")
public class PayPalController {

	private final PayPalClient payPalClient;

	@Autowired
	PayPalController(PayPalClient payPalClient) {
		this.payPalClient = payPalClient;
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping(value = "/make/payment")
	public Map<String, Object> makePayment(@RequestParam("sum") String sum) {
		return payPalClient.createPayment(sum);
	}

	@CrossOrigin(origins = "http://localhost:4200")
	@GetMapping(value = "/complete/payment", produces = MediaType.APPLICATION_JSON_VALUE)
	public String completePayment(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId) {
		return payPalClient.completePayment(paymentId, payerId);
	}

}
