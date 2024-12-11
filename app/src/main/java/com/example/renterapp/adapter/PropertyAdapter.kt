package com.example.renterapp.adapter

import android.location.Geocoder
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.renterapp.R
import com.example.renterapp.databinding.PropertyDetailPopupLayoutBinding
import com.example.renterapp.databinding.PropertyDetailRowLayoutBinding
import com.example.renterapp.model.Property

class PropertyAdapter(val propertyList: MutableList<Property>, val geocoder: Geocoder, val clickInterface: ClickInterface): RecyclerView.Adapter<PropertyAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: PropertyDetailRowLayoutBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PropertyDetailRowLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return propertyList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curProperty = propertyList[position]

        val searchResult = geocoder.getFromLocation(curProperty.location.latitude, curProperty.location.longitude, 1)
        if(searchResult!=null && searchResult.isNotEmpty()){
            holder.binding.tvAddress.text = searchResult[0].getAddressLine(0)
        }

        holder.binding.tvDescription.text = curProperty.description
        holder.binding.tvPrice.text = "$${curProperty.price} CAD"
        holder.binding.tvBedrooms.text = "${curProperty.bedrooms} Bedrooms"
        holder.binding.tvStatus.text = if(curProperty.isAvailable) "Available" else "Unavailable"
        holder.binding.tvStatus.setTextColor(if(curProperty.isAvailable) holder.binding.root.context.getColor(
            R.color.green) else holder.binding.root.context.getColor(R.color.red))

        holder.binding.btnRemove.setOnClickListener {
            clickInterface.removeFromWishlist(curProperty.id)
        }
    }
}