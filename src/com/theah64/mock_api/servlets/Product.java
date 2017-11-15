package com.theah64.mock_api.servlets;

import java.util.List;

/**
 * Created by theapache64 on 15/11/17.
 */
public class Product {

    private final String title;
    private final String brand;
    private final String thumbUrl;
    private final String imageUrl;
    private final List<Quantity> quantities;
    private final String description;
    private final String categoryName;
    private final String quantityTitle;
    private final List<SimilarProduct> similarProducts;

    public Product(String title,String brand,String thumbUrl,String imageUrl,List<Quantity> quantities,String description,String categoryName,String quantityTitle,List<SimilarProduct> similarProducts){
        this.title = title;
        this.brand = brand;
        this.thumbUrl = thumbUrl;
        this.imageUrl = imageUrl;
        this.quantities = quantities;
        this.description = description;
        this.categoryName = categoryName;
        this.quantityTitle = quantityTitle;
        this.similarProducts = similarProducts;
    }

    public String getTitle(){
        return title;
    }

    public String getBrand(){
        return brand;
    }

    public String getThumbUrl(){
        return thumbUrl;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public List<Quantity> getQuantities(){
        return quantities;
    }

    public String getDescription(){
        return description;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public String getQuantityTitle(){
        return quantityTitle;
    }

    public List<SimilarProduct> getSimilarProducts(){
        return similarProducts;
    }

    public static class Quantity {

        private final String id;
        private final String povId;
        private final String quantity;
        private final String cartCount;
        private final String realPrice;
        private final String anonaPrice;
        private final String availability;
        private final String rewardsNeeded;
        private final String favoritesCount;
        private final String purchasedCount;

        public Quantity(String id,String povId,String quantity,String cartCount,String realPrice,String anonaPrice,String availability,String rewardsNeeded,String favoritesCount,String purchasedCount){
            this.id = id;
            this.povId = povId;
            this.quantity = quantity;
            this.cartCount = cartCount;
            this.realPrice = realPrice;
            this.anonaPrice = anonaPrice;
            this.availability = availability;
            this.rewardsNeeded = rewardsNeeded;
            this.favoritesCount = favoritesCount;
            this.purchasedCount = purchasedCount;
        }

        public String getId(){
            return id;
        }

        public String getPovId(){
            return povId;
        }

        public String getQuantity(){
            return quantity;
        }

        public String getCartCount(){
            return cartCount;
        }

        public String getRealPrice(){
            return realPrice;
        }

        public String getAnonaPrice(){
            return anonaPrice;
        }

        public String getAvailability(){
            return availability;
        }

        public String getRewardsNeeded(){
            return rewardsNeeded;
        }

        public String getFavoritesCount(){
            return favoritesCount;
        }

        public String getPurchasedCount(){
            return purchasedCount;
        }

    }

    public static class SimilarProduct {

        private final String id;
        private final String title;
        private final String thumbUrl;

        public SimilarProduct(String id,String title,String thumbUrl){
            this.id = id;
            this.title = title;
            this.thumbUrl = thumbUrl;
        }

        public String getId(){
            return id;
        }

        public String getTitle(){
            return title;
        }

        public String getThumbUrl(){
            return thumbUrl;
        }

    }



}


