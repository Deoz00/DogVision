package com.example.dogvision

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.example.dogvision.ml.Modelo
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class tensorActivity : AppCompatActivity() {

    lateinit var text : TextView
    lateinit var resizedBitmap : Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tensor)

        val intent = intent

        // Obtener el String enviado como extra en el Intent
        val imageUriString = intent.getStringExtra("imageUri")
        val uri: Uri = Uri.parse(imageUriString)




        val imageView: ImageView = findViewById(R.id.imageView)
        text = findViewById<TextView>(R.id.textView2)


        // Verificar si la URI no es nula
        if (imageUriString != null) {
            // Mostrar la imagen en el ImageView
            imageView.setImageURI(uri)
        } else {
            // Manejar el caso en que la URI es nula
            // Por ejemplo, mostrar un mensaje de error
            // o cargar una imagen predeterminada
        }

        val bitmap = loadBitmapFromUri(uri)
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, 299, 299, false)


    }

    override fun onResume() {
        super.onResume()
        // Tu código aquí se ejecutará cuando la actividad esté lista para interactuar

        
        classifyImage(resizedBitmap)
    }


    private fun classifyImage(image: Bitmap) {
        val model = Modelo.newInstance(applicationContext)

// Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 299, 299, 3), DataType.FLOAT32)

        val byteBuffer = ByteBuffer.allocateDirect(4 * 299 * 299 * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(299 * 299)
        image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
        var pixel = 0
        //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
        //iterate over each pixel and extract R, G, and B values. Add those values individually to the byte buffer.
        for (i in 0 until 299) {
            for (j in 0 until 299) {
                val `val` = intValues[pixel++] // RGB
                byteBuffer.putFloat((`val` shr 16 and 0xFF) * (1f / 255))
                byteBuffer.putFloat((`val` shr 8 and 0xFF) * (1f / 255))
                byteBuffer.putFloat((`val` and 0xFF) * (1f / 255))
            }
        }



        inputFeature0.loadBuffer(byteBuffer)

// Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer


        val confidences = outputFeature0.floatArray
        // find the index of the class with the biggest confidence.
        // find the index of the class with the biggest confidence.
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }
        println(maxConfidence)

        val breed = listOf(
            "Afghan_hound", "African_hunting_dog", "Airedale",
            "American_Staffordshire_terrier", "Appenzeller",
            "Australian_terrier", "Bedlington_terrier", "Bernese_mountain_dog",
            "Blenheim_spaniel", "Border_collie", "Border_terrier",
            "Boston_bull", "Bouvier_des_Flandres", "Brabancon_griffon",
            "Brittany_spaniel", "Cardigan", "Chesapeake_Bay_retriever",
            "Chihuahua", "Dandie_Dinmont", "Doberman", "English_foxhound",
            "English_setter", "English_springer", "EntleBucher", "Eskimo_dog",
            "French_bulldog", "German_shepherd", "German_short",
            "Gordon_setter", "Great_Dane", "Great_Pyrenees",
            "Greater_Swiss_Mountain_dog", "Ibizan_hound", "Irish_setter",
            "Irish_terrier", "Irish_water_spaniel", "Irish_wolfhound",
            "Italian_greyhound", "Japanese_spaniel", "Kerry_blue_terrier",
            "Labrador_retriever", "Lakeland_terrier", "Leonberg", "Lhasa",
            "Maltese_dog", "Mexican_hairless", "Newfoundland",
            "Norfolk_terrier", "Norwegian_elkhound", "Norwich_terrier",
            "Old_English_sheepdog", "Pekinese", "Pembroke", "Pomeranian",
            "Rhodesian_ridgeback", "Rottweiler", "Saint_Bernard", "Saluki",
            "Samoyed", "Scotch_terrier", "Scottish_deerhound",
            "Sealyham_terrier", "Shetland_sheepdog", "Shih", "Siberian_husky",
            "Staffordshire_bullterrier", "Sussex_spaniel", "Tibetan_mastiff",
            "Tibetan_terrier", "Walker_hound", "Weimaraner",
            "Welsh_springer_spaniel", "West_Highland_white_terrier",
            "Yorkshire_terrier", "affenpinscher", "basenji", "basset",
            "beagle", "black", "bloodhound", "bluetick", "borzoi", "boxer",
            "briard", "bull_mastiff", "cairn", "chow", "clumber",
            "cocker_spaniel", "collie", "curly", "dhole", "dingo", "flat",
            "giant_schnauzer", "golden_retriever", "groenendael", "keeshond",
            "kelpie", "komondor", "kuvasz", "malamute", "malinois",
            "miniature_pinscher", "miniature_poodle", "miniature_schnauzer",
            "otterhound", "papillon", "pug", "redbone", "schipperke",
            "silky_terrier", "soft", "standard_poodle", "standard_schnauzer",
            "toy_poodle", "toy_terrier", "vizsla", "whippet", "wire"
        )
        println(breed[maxPos])
        Log.e("TAG", breed[maxPos])
        text.text = breed[maxPos]




// Releases model resources if no longer used.
        model.close()
    }

    private fun loadBitmapFromUri(uri: Uri): Bitmap {
        val contentResolver = applicationContext.contentResolver
        val source = ImageDecoder.createSource(contentResolver, uri)
        return ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
            decoder.isMutableRequired = true
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    }

}