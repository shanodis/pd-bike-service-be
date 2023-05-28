package me.project.dtos.request;

import com.google.gson.annotations.SerializedName;

    public class StripePaymentRequest {
        @SerializedName("items")
        Object[] items;

        public Object[] getItems() {
            return items;
        }
    }
