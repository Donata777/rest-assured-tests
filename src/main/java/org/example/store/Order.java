package org.example.store;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;

public class Order {

	@SerializedName("id")
	private int id;

	@SerializedName("petId")
	private int petId;

	@SerializedName("quantity")
	private int quantity;

	@SerializedName("shipDate")
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	private OffsetDateTime shipDate;

	@SerializedName("status")
	private Status status;

	@SerializedName("complete")
	private boolean complete;

	public enum Status {
		PLACED, APPROVED, DELIVERED
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}

	public void setPetId(int petId){
		this.petId = petId;
	}

	public int getPetId(){
		return petId;
	}

	public void setQuantity(int quantity){
		this.quantity = quantity;
	}

	public int getQuantity(){
		return quantity;
	}

	public void setShipDate(OffsetDateTime shipDate){
		this.shipDate = shipDate;
	}

	public OffsetDateTime getShipDate(){
		return shipDate;
	}

	public void setStatus(Status status){
		this.status = status;
	}

	public Status getStatus(){
		return status;
	}

	public void setComplete(boolean complete){
		this.complete = complete;
	}

	public boolean isComplete(){
		return complete;
	}

	@Override
	public String toString(){
		return
				"Order{" +
						"id = '" + id + '\'' +
						",petId = '" + petId + '\'' +
						",quantity = '" + quantity + '\'' +
						",shipDate = '" + shipDate + '\'' +
						",status = '" + status + '\'' +
						",complete = '" + complete + '\'' +
						"}";
	}
}