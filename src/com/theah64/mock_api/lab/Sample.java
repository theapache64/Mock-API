package com.theah64.mock_api.lab;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sample {

    @SerializedName("data")
    private final Data data;

    @SerializedName("error")
    private final boolean error;

    @SerializedName("message")
    private final String message;


    public Sample(Data data,boolean error,String message){
        this.data = data;
        this.error = error;
        this.message = message;
    }

    public Data getData(){
        return data;
    }

    public boolean isError(){
        return error;
    }

    public String getMessage(){
        return message;
    }

    public static class Data {

        @SerializedName("prices")
        private final List<Price> prices;

        @SerializedName("accounts")
        private final Accounts accounts;

        @SerializedName("customers")
        private final List<Customer> customers;

        @SerializedName("countries")
        private final List<Country> countries;

        @SerializedName("sales_persons")
        private final List<SalesPerson> salesPersons;

        @SerializedName("payment_terms")
        private final List<PaymentTerm> paymentTerms;

        @SerializedName("customer_types")
        private final List<CustomerType> customerTypes;

        @SerializedName("shipping_types")
        private final List<ShippingType> shippingTypes;

        @SerializedName("customer_groups")
        private final List<CustomerGroup> customerGroups;


        public Data(List<Price> prices,Accounts accounts,List<Customer> customers,List<Country> countries,List<SalesPerson> salesPersons,List<PaymentTerm> paymentTerms,List<CustomerType> customerTypes,List<ShippingType> shippingTypes,List<CustomerGroup> customerGroups){
            this.prices = prices;
            this.accounts = accounts;
            this.customers = customers;
            this.countries = countries;
            this.salesPersons = salesPersons;
            this.paymentTerms = paymentTerms;
            this.customerTypes = customerTypes;
            this.shippingTypes = shippingTypes;
            this.customerGroups = customerGroups;
        }

        public List<Price> getPrices(){
            return prices;
        }

        public Accounts getAccounts(){
            return accounts;
        }

        public List<Customer> getCustomers(){
            return customers;
        }

        public List<Country> getCountries(){
            return countries;
        }

        public List<SalesPerson> getSalesPersons(){
            return salesPersons;
        }

        public List<PaymentTerm> getPaymentTerms(){
            return paymentTerms;
        }

        public List<CustomerType> getCustomerTypes(){
            return customerTypes;
        }

        public List<ShippingType> getShippingTypes(){
            return shippingTypes;
        }

        public List<CustomerGroup> getCustomerGroups(){
            return customerGroups;
        }

    }

    public static class ShippingType {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public ShippingType(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class CustomerType {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public CustomerType(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class Price {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public Price(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class PaymentTerm {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public PaymentTerm(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class Accounts {

        @SerializedName("gl_accounts")
        private final List<GlAccount> glAccounts;

        @SerializedName("bd_accounts")
        private final List<BdAccount> bdAccounts;


        public Accounts(List<GlAccount> glAccounts,List<BdAccount> bdAccounts){
            this.glAccounts = glAccounts;
            this.bdAccounts = bdAccounts;
        }

        public List<GlAccount> getGlAccounts(){
            return glAccounts;
        }

        public List<BdAccount> getBdAccounts(){
            return bdAccounts;
        }

    }

    public static class BdAccount {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public BdAccount(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class GlAccount {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public GlAccount(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class Country {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;

        @SerializedName("states")
        private final List<State> states;


        public Country(String id,String name,List<State> states){
            this.id = id;
            this.name = name;
            this.states = states;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

        public List<State> getStates(){
            return states;
        }

    }

    public static class State {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public State(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class Customer {

        @SerializedName("id")
        private final String id;

        @SerializedName("customer_code")
        private final String customerCode;


        public Customer(String id,String customerCode){
            this.id = id;
            this.customerCode = customerCode;
        }

        public String getId(){
            return id;
        }

        public String getCustomerCode(){
            return customerCode;
        }

    }

    public static class SalesPerson {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public SalesPerson(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }

    public static class CustomerGroup {

        @SerializedName("id")
        private final String id;

        @SerializedName("name")
        private final String name;


        public CustomerGroup(String id,String name){
            this.id = id;
            this.name = name;
        }

        public String getId(){
            return id;
        }

        public String getName(){
            return name;
        }

    }



}