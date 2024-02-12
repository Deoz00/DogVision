package com.example.dogvision

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView

import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat

import androidx.appcompat.app.AppCompatActivity
//import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import androidx.core.app.ActivityCompat


import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

import java.io.File
import java.text.SimpleDateFormat
import java.util.*

import androidx.camera.core.*





// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Home.newInstance] factory method to
 * create an instance of this fragment.
 */
class Home : Fragment() {

//camara

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var flashEnabled = false
    private var camera: Camera? = null
    lateinit var btnFlash : AppCompatImageButton

    // image picker

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->

        if (uri!=null){
            //imagen seleccionada
            Log.i("aris", "seleccionado")
            // Pasar la URI a PreviewActivity
            val intent = Intent(requireContext(), tensorActivity::class.java).apply {
                putExtra("imageUri", uri.toString())
            }
            startActivity(intent)
        }else{
            //no imagen
            Log.i("aris", "no seleccionado")

        }

    }

    private lateinit var btnPickImage: ImageButton



    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var previewView: PreviewView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }



    var imageView : ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?




    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home, container, false)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        val btnCapture = view.findViewById<AppCompatImageButton>(R.id.capture)
        btnPickImage = view.findViewById(R.id.pick)

        btnFlash = view.findViewById(R.id.toggleFlash)


        outputDirectory = requireContext().getExternalFilesDir("photos")!!
        cameraExecutor = Executors.newSingleThreadExecutor()

        btnCapture.setOnClickListener { takePhoto() }
        btnFlash.setOnClickListener { toggleFlash() }

        btnPickImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }



        startCamera()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind any existing use cases before rebinding
                cameraProvider.unbindAll()




                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

                // Bind use cases to camera
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Error starting camera: ${exc.message}", exc)
                Toast.makeText(
                    requireContext(),
                    "Error starting camera: ${exc.message}",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun toggleFlash() {
        val cameraControl = camera?.cameraControl ?: return

        flashEnabled = !flashEnabled
        cameraControl.enableTorch(flashEnabled)
        if (flashEnabled) {
            btnFlash.setImageResource(R.drawable.baseline_flash_off_24)
        } else {
            btnFlash.setImageResource(R.drawable.baseline_flash_on_24)
        }
    }



    private fun takePhoto() {

        val imageCapture = imageCapture ?: return

        val photoFile = File(
            outputDirectory,
            "photo_${System.currentTimeMillis()}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext().applicationContext,
                            "Error capturing image: ${exc.message}",
                            Toast.LENGTH_SHORT
                        ).show()


                    }
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    requireActivity().runOnUiThread {
                        Toast.makeText(
                            requireContext().applicationContext,
                            "Image captured: $savedUri",
                            Toast.LENGTH_SHORT
                        ).show()

                        Log.e("TAG", savedUri.toString())


                        // Pasar la URI a PreviewActivity
                        val intent = Intent(requireContext(), tensorActivity::class.java).apply {
                            putExtra("imageUri", savedUri.toString())
                        }
                        startActivity(intent)
                    }
                }
            }
        )
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }






}