package com.hippocompany.myeikcoronareportserver

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException


class FabActivity : AppCompatActivity() {
    lateinit var cardImage: ImageView
    private val PICK_IMAGE_REQUEST = 1
    lateinit var edtCpation: TextInputEditText
    lateinit var edtDate: TextInputEditText
    lateinit var edtBody: TextInputEditText
    lateinit var btnUpload: MaterialButton
    var progressDialog: ProgressDialog? = null
    var ImageUploadId: String? = ""
    var Database_Path = "All_News_Uploads_Database/UpdateNews"
    var Storage_Path = "All_News_Images_Uploads/"


    lateinit var storageReference: StorageReference
    lateinit var databaseReference: DatabaseReference
    var filePathUri: Uri? = null
    var caption: String = ""
    var date: String = ""
    var body: String = ""
    var imageUrl: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fab)
        init()
        cardImageClicked()

        btnUpload.setOnClickListener(View.OnClickListener { v: View? ->
            uploadImagetoFireBaseStroage()
        })
    }

    fun init() {
        cardImage = findViewById(R.id.imgView_card)
        edtCpation = findViewById(R.id.edt_caption)
        edtDate = findViewById(R.id.edt_source_of_news)
        edtBody = findViewById(R.id.edt_body)
        btnUpload = findViewById(R.id.btn_Upload)

        // Assigning Id to ProgressDialog.
        progressDialog = ProgressDialog(this)
        storageReference = FirebaseStorage.getInstance().getReference()

        // Assign FirebaseDatabase instance with root database name.
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path)

    }

    fun cardImageClicked() {
        cardImage.setOnClickListener { v: View? ->
            chooseImage()
        }
    }

    fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePathUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePathUri)
                Log.d("TAG", (bitmap).toString())

                cardImage.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun uploadImagetoFireBaseStroage() {
        if (filePathUri != null && edtCpation.text.toString().isNotEmpty() &&
            edtDate.text.toString().isNotEmpty() && edtBody.text.toString().isNotEmpty()
        ) {
            progressDialog!!.setTitle("News is Uploading")
            progressDialog!!.show()

            // Creating second StorageReference.
            val storageReference2nd = storageReference.child(
                Storage_Path + System.currentTimeMillis() + "." + GetFileExtension(
                    filePathUri
                )
            )

            storageReference2nd.putFile(filePathUri!!).addOnSuccessListener {
                storageReference2nd.getDownloadUrl()
                    .addOnSuccessListener(object : OnSuccessListener<Uri?> {
                        override fun onSuccess(uri: Uri?) {
                            caption = edtCpation.text.toString().trim()
                            date = edtDate.text.toString().trim()
                            body = edtBody.text.toString().trim()

                            var uploadData: UploadData =
                                UploadData(
                                    caption,
                                    date,
                                    body,
                                    uri.toString()
                                )
                            Toast.makeText(
                                applicationContext,
                                "Image Uploaded Successfully ",
                                Toast.LENGTH_LONG
                            ).show()
                            val imageUploadId = databaseReference.push().key
                            databaseReference.child(imageUploadId!!).setValue(uploadData)
                            progressDialog!!.dismiss()
                            finish()

                        }


                    })
            }
                .addOnFailureListener { exception -> Log.e(exception.toString(), "Expception") }
        } else {
            Toast.makeText(
                this,
                " OOPS!! Somethings is missing ",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun GetFileExtension(uri: Uri?): String? {
        val contentResolver = contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    override fun onBackPressed() {
        val dialogBuilder = MaterialAlertDialogBuilder(this)
        dialogBuilder.setTitle("Are You Sure");
        dialogBuilder.setMessage("Do you want to Cancel News? ")
        dialogBuilder.setPositiveButton("Sure") { dialog: DialogInterface?, which: Int ->
            super.onBackPressed()
        }
        dialogBuilder.setNegativeButton("No") { dialog: DialogInterface?, which: Int ->
            dialog?.dismiss()
        }
        dialogBuilder.show()
    }
}
