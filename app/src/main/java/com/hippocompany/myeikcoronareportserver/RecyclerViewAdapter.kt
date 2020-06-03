package com.hippocompany.coronareport

import DataModel
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.hippocompany.myeikcoronareportserver.R

class RecyclerViewAdapter(val dataModel: ArrayList<DataModel>) :

    RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {
    lateinit var context: Context

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(viewGroup?.context)
            .inflate(R.layout.custom_new_card_item, viewGroup, false)
        return MyViewHolder(view);
    }

    override fun getItemCount(): Int {
        return dataModel.size
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        context = viewHolder.itemView.context

        viewHolder.caption?.text = dataModel[position].caption
        viewHolder.date?.text = dataModel[position].postDate
        Glide.with(context).load(dataModel[position].imageCardView)
            .into(viewHolder.cardImage)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val caption: TextView
        val date: TextView
        val cardImage: ImageView
        val seeMorebutton: MaterialButton

        init {
            caption = itemView.findViewById<TextView>(R.id.textView_cardCaption)
            date = itemView.findViewById<TextView>(R.id.textView_card_source_of_news)
            cardImage = itemView.findViewById<ImageView>(R.id.imageView_cardImage)
            seeMorebutton = itemView.findViewById(R.id.btn_SeeMore)

            itemView.setOnClickListener { v: View? ->
                var position: Int = adapterPosition
                Toast.makeText(v?.context, "$position", Toast.LENGTH_SHORT).show()
            }
            seeMorebutton.setOnClickListener { v: View? ->
                var position: Int = adapterPosition
                Toast.makeText(v?.context, "$position", Toast.LENGTH_SHORT).show()
            }
            itemView.setOnLongClickListener { v: View? ->
                var position: Int = adapterPosition
                val dialogBuilder = MaterialAlertDialogBuilder(v?.context)
                dialogBuilder.setTitle("Are You Sure");
                dialogBuilder.setMessage("Do you want to delete News? ")
                dialogBuilder.setPositiveButton("Delete") { dialog: DialogInterface?, which: Int ->
                    //Delete
                    val databaseReference: DatabaseReference = FirebaseDatabase.getInstance()
                        .getReference("All_News_Uploads_Database/UpdateNews")
                   /* val query: Unit =
                        databaseReference.orderByChild("caption").equalTo(position.toString())
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {
                                    dialog?.dismiss()
                                }

                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        val firstChild = dataSnapshot.children.iterator().next()
                                        firstChild.ref.removeValue()
                                    }

                                }

                            })*/

                }
                dialogBuilder.setNegativeButton("Update") { dialog: DialogInterface?, which: Int ->
                    dialog?.dismiss()
                }
                dialogBuilder.show()
                return@setOnLongClickListener true
            }

        }

    }
}