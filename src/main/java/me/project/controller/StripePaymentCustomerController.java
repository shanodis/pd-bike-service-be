package me.project.controller;

import me.project.stripeModel.CustomerData;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class StripePaymentCustomerController {

    @Value("${stripe.apikey}")
    String stripeKey;

    @RequestMapping("api/v1/createCustomer")
    public CustomerData payment(@RequestBody CustomerData data) throws StripeException {
        Stripe.apiKey = stripeKey;
        Map<String, Object> params = new HashMap<>();
        params.put("name", data.getName());
        params.put("email", data.getEmail());


        Customer customer = Customer.create(params);

        data.setCustomerId(customer.getId());
        return data;
    }
}
