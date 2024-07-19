package com.example.shoppers.ui.addProduct

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.shoppers.R
import com.example.shoppers.base.util.ConstValues
import com.example.shoppers.databinding.FragmentAddProductBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream

class AddProductFragment : Fragment() {
   private lateinit var binding: FragmentAddProductBinding
    private var selectedImageBitmap: Bitmap? = null
    private val PICK_IMAGE_REQUEST = 71
    lateinit var auth: FirebaseAuth
    lateinit var firestore: FirebaseFirestore
    lateinit var storage: FirebaseStorage

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAddProductBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth=Firebase.auth
        firestore=Firebase.firestore
        storage=Firebase.storage
        addImage()
        selectedImage()
        initListeners()
    }


    private fun selectedImage() {
        binding.imgProduct.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val selectedImageUri = data.data
            selectedImageBitmap =
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    selectedImageUri
                )
            binding.imgProduct.setImageBitmap(selectedImageBitmap)
        } else {
            Toast.makeText(requireContext(), "Something gone wrong", Toast.LENGTH_SHORT)
                .show()
            findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
        }
    }


    private fun addImage() {
        binding.btnAddProduct.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            val price = binding.edtPrice.text.toString()
            val product=binding.edtNameProduct.text.toString()
            val detail=binding.edtDetail.text.toString()
            val user = auth.currentUser!!.uid

            if (selectedImageBitmap != null) {
                val ref = firestore.collection("Products").document()
                val productId = ref.id

                val productMap = hashMapOf<String, Any>(
                    "product" to product,
                    ConstValues.USER_ID to user,
                    "detail" to detail,
                    "price" to price,
                    "productId" to productId
                )

                uploadImage(productId, productMap, ref)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select an image",
                    Toast.LENGTH_SHORT
                )
                    .show()
                binding.progressBar.visibility = View.GONE

            }
        }
    }

    private fun uploadImage(
        productId: String,
        productMap: HashMap<String, Any>,
        ref: DocumentReference
    ) {
        selectedImageBitmap?.let { bitmap ->
            val boas = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, boas)
            val imageData = boas.toByteArray()

            val storageRef = storage.reference.child("images").child("$productId.jpg")
            storageRef.putBytes(imageData)
                .addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val downloadUrl = uri.toString()
                        productMap["productImageUrl"] = downloadUrl
                        addProductInfoToFireStore(productMap, ref)
                    }
                }.addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                       "Failed to upload image",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        }
    }

    private fun addProductInfoToFireStore(postMap: HashMap<String, Any>, ref: DocumentReference) {
        ref.set(postMap)
            .addOnSuccessListener {
                findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed!", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addProductFragment_to_homeFragment)
            }
    }


    private fun initListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}