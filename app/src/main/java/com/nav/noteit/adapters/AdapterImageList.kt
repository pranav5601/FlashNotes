package com.nav.noteit.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.nav.noteit.R
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import kotlin.math.floor

class AdapterImageList(private val context: Context,private val imageClickListener: ImageClickListener) : RecyclerView.Adapter<AdapterImageList.ImageViewHolder>() {

      private var imageList = ArrayList<String>()


    class ImageViewHolder(itemView: View,val context: Context): RecyclerView.ViewHolder(itemView) {

        val lytImgList = itemView.findViewById<LinearLayout>(R.id.lytImgList)
        val imgListForNote = itemView.findViewById<ShapeableImageView>(R.id.imgListForNote)


        fun bindings(
            image: String,
            position: Int,
            context: Context,
            clickListener: ImageClickListener
        ){
            Glide.with(context).load(Uri.parse(image)).into(imgListForNote)

            lytImgList.setOnClickListener {
                clickListener.onImageClick(position)
            }
            lytImgList.setOnLongClickListener {
                clickListener.onLongImageClick(position)
                true
            }
        }


    }

    fun updateImages(images: List<String>){
        imageList.clear()
        imageList.addAll(images)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val imageView = LayoutInflater.from(context).inflate(R.layout.cell_image,parent, false)
        return ImageViewHolder(imageView, context)
    }

    override fun getItemCount() = if(imageList.isNullOrEmpty()) 0 else imageList.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        imageList.let {
            holder.bindings(it[position], position,context,imageClickListener)
        }

    }

    interface ImageClickListener{
        fun onImageClick(imagePos: Int)
        fun onLongImageClick(imagePos: Int)
    }

}