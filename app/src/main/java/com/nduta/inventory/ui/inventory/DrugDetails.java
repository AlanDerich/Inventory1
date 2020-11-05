package com.nduta.inventory.ui.inventory;

public class DrugDetails {
    String drugName,DrugAmount,DrugPrice;

    public DrugDetails() {
    }

    public DrugDetails(String drugName, String drugAmount, String drugPrice) {
        this.drugName = drugName;
        DrugAmount = drugAmount;
        DrugPrice = drugPrice;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDrugAmount() {
        return DrugAmount;
    }

    public void setDrugAmount(String drugAmount) {
        DrugAmount = drugAmount;
    }

    public String getDrugPrice() {
        return DrugPrice;
    }

    public void setDrugPrice(String drugPrice) {
        DrugPrice = drugPrice;
    }
}
