package com.uniwaylabs.buildo.ui.materialSpecification

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import com.uniwaylabs.buildo.R
import com.uniwaylabs.buildo.ui.adminForms.ACTIVITY_DATA
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.ImageSliderFragment
import com.uniwaylabs.buildo.ui.commans.ImageSliderFragment.SLIDING_IMAGES
import java.io.Serializable



class FullScreenImageActivity: AppCompatActivity() {

    private var imageContainer: FrameLayout? = null
    private var images: Array<String> = emptyArray()

    class ActivityData(images: Array<String>, selectedPosition: Int?): Serializable{
        var images: Array<String>? = null
        var selectedPosition: Int? = null

        init {
            this.images = images
            this.selectedPosition = selectedPosition
        }
    }

    class Contract: ActivityResultContract<ActivityData, String>(){

        override fun createIntent(context: Context, input: ActivityData): Intent = Intent(context, com.uniwaylabs.buildo.ui.materialSpecification.FullScreenImageActivity::class.java)
            .putExtra(ACTIVITY_DATA, input)

        override fun parseResult(resultCode: Int, intent: Intent?): String {
            return "Ok"
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)
        imageContainer = findViewById<View>(R.id.item_image_container) as FrameLayout
        images = getSerializable(intent, ACTIVITY_DATA, ActivityData::class.java)?.images ?: emptyArray()
        findViewById<ImageButton>(R.id.backbutton).setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        setupFragment()
    }


    private fun <T : Serializable?> getSerializable(intent: Intent, name: String, clazz: Class<T>): T?
    {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            intent.getSerializableExtra(name, clazz)
        else
            intent.getSerializableExtra(name) as T?
    }


    private fun setupFragment(){
        if (imageContainer != null){
            val fragment = ImageSliderFragment()
            fragment.arguments = Bundle().apply { putStringArray(
                SLIDING_IMAGES, images) }
            supportFragmentManager.beginTransaction().add(R.id.item_image_container, fragment).commit()
        }
    }
}