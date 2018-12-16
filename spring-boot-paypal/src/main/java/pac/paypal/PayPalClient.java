package pac.paypal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.Transaction;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;

@Component
public class PayPalClient {

	String clientId = "AUcN42UDtFlG_ZFsec5Tf_QG_aFol1pZ_MA7JhWXeyONHHf0BgtP2bzuJrCHRS5iX7FA0E9l_zgdjMoS";
	String clientSecret = "EIAQS2-l9Yth18MJl4IXj1rPx-MHDhUalG1-ivNT-qnymDv1rB9rN63rC_iWp3Xqoxaj1bkUMHjEsYup";

	// @Autowired
	// PayPalClient(){}

	public Map<String, Object> createPayment(String sum) {
		Map<String, Object> response = new HashMap<String, Object>();
		Amount amount = new Amount();
		amount.setCurrency("USD");
		amount.setTotal(sum);
		Transaction transaction = new Transaction();
		transaction.setAmount(amount);
		List<Transaction> transactions = new ArrayList<Transaction>();
		transactions.add(transaction);

		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");

		Payment payment = new Payment();
		payment.setIntent("sale");
		payment.setPayer(payer);
		payment.setTransactions(transactions);

		RedirectUrls redirectUrls = new RedirectUrls();
		redirectUrls.setCancelUrl("http://localhost:4200/cancel");
		redirectUrls.setReturnUrl("http://localhost:4200/paypal/complete/payment");
		payment.setRedirectUrls(redirectUrls);
		Payment createdPayment;
		try {
			String redirectUrl = "";
			APIContext context = new APIContext(clientId, clientSecret, "sandbox");
			createdPayment = payment.create(context);
			if (createdPayment != null) {
				List<Links> links = createdPayment.getLinks();
				for (Links link : links) {
					if (link.getRel().equals("approval_url")) {
						redirectUrl = link.getHref();
						break;
					}
				}
				response.put("status", "success");
				response.put("redirect_url", redirectUrl);
			}
		} catch (PayPalRESTException e) {
			System.out.println("Error happened during payment creation!");
		}
		return response;
	}

	public String completePayment(String paymentId, String payerId) {
		Payment payment = new Payment();
		payment.setId(paymentId);
		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerId);
		try {
			APIContext context = new APIContext(clientId, clientSecret, "sandbox");
			Payment createdPayment = payment.execute(context, paymentExecution);
			if (createdPayment != null) {
				return createdPayment.toJSON().toString();
			}
		} catch (PayPalRESTException e) {
			System.err.println(e.getDetails());
			return e.getDetails().toJSON().toString();
		}
		// this will be invalid case
		return null;
	}

}
