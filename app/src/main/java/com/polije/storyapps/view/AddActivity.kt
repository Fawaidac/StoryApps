package com.polije.storyapps.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.polije.storyapps.FileUtil
import com.polije.storyapps.R
import com.polije.storyapps.network.Config
import com.polije.storyapps.model.StoryResponse
import com.polije.storyapps.getImageUri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddActivity : AppCompatActivity() {

    private lateinit var cameraButton: AppCompatButton
    private lateinit var galeryButton: AppCompatButton
    private lateinit var photoImageView: ImageView
    private lateinit var uploadButton: AppCompatButton
    private lateinit var descEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences
    private var currentImageUri: Uri? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        cameraButton = findViewById(R.id.btn_camera)
        galeryButton = findViewById(R.id.btn_galery)
        photoImageView = findViewById(R.id.iv_item_photo)
        uploadButton = findViewById(R.id.button_add)
        descEditText = findViewById(R.id.ed_add_description)

        galeryButton.setOnClickListener {
            startGallery()
        }
        cameraButton.setOnClickListener {
            startCamera()
        }
        uploadButton.setOnClickListener {
            if (token != null) {
                uploadStory(token)
            }
        }
    }

    private fun startGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            if (uri != null) {
                currentImageUri = uri
                Log.d("DetailActivity", "Selected image URI: $uri")
                showImage()
            } else {
                Log.d("DetailActivity", "No image selected")
            }
        } else {
            Log.d("DetailActivity", "Gallery activity canceled")
        }
    }



    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            photoImageView.setImageURI(it)
        }
    }


    private fun uploadStory(token: String) {
        val description = descEditText.text.toString().trim()
        if (description.isEmpty()) {
            showToast("Please enter description")
            return
        }
        if (currentImageUri == null) {
            showToast("Please select an image")
            return
        }
        val imageFile = FileUtil.getFile(this, currentImageUri)
        if (imageFile == null || !imageFile.exists()) {
            showToast("Failed to get image file")
            return
        }
        val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("photo", imageFile.name, imageRequestBody)

        val descriptionRequestBody = description.toRequestBody()
        addStory(token, descriptionRequestBody, imagePart)
    }
    private fun addStory(
        token: String,
        description: RequestBody,
        photo: MultipartBody.Part,
    ) {
        val service = Config.getServices()
        val call = service.addStory("Bearer $token", description, photo)
        call.enqueue(object : Callback<StoryResponse> {
            override fun onResponse(call: Call<StoryResponse>, response: Response<StoryResponse>) {
                if (response.isSuccessful) {
                    val addStoryResponse = response.body()
                    if (addStoryResponse != null && !addStoryResponse.error) {
                        showToast("Story added successfully")
                        val intent = Intent(this@AddActivity, HomeActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        showToast("Failed to add story: ${addStoryResponse?.message}")
                    }
                } else {
                    showToast("Failed to add story")
                }
            }

            override fun onFailure(call: Call<StoryResponse>, t: Throwable) {
                showToast("Failed to add story: ${t.message}")
            }
        })
    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}