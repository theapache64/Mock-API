package com.theah64.mock_api.lab

import com.google.gson.annotations.SerializedName

class Sample(
        @SerializedName("data")
        val data: Data,
        @SerializedName("error")
        val isError: Boolean,
        @SerializedName("message")
        val message: String
) {

    class Data(
            @SerializedName("prices")
            val prices: List<Price>,
            @SerializedName("accounts")
            val accounts: Accounts,
            @SerializedName("customers")
            val customers: List<Customer>,
            @SerializedName("countries")
            val countries: List<Country>,
            @SerializedName("sales_persons")
            val salesPersons: List<SalesPerson>,
            @SerializedName("payment_terms")
            val paymentTerms: List<PaymentTerm>,
            @SerializedName("customer_types")
            val customerTypes: List<CustomerType>,
            @SerializedName("shipping_types")
            val shippingTypes: List<ShippingType>,
            @SerializedName("customer_groups")
            val customerGroups: List<CustomerGroup>
    )

    class ShippingType(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class CustomerType(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class Price(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class PaymentTerm(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class Accounts(
            @SerializedName("gl_accounts")
            val glAccounts: List<GlAccount>,
            @SerializedName("bd_accounts")
            val bdAccounts: List<BdAccount>
    )

    class BdAccount(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class GlAccount(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class Country(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String,
            @SerializedName("states")
            val states: List<State>
    )

    class State(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class Customer(
            @SerializedName("id")
            val id: String,
            @SerializedName("customer_code")
            val customerCode: String
    )

    class SalesPerson(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )

    class CustomerGroup(
            @SerializedName("id")
            val id: String,
            @SerializedName("name")
            val name: String
    )


}