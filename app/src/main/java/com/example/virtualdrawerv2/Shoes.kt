package com.example.virtualdrawerv2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import com.example.virtualdrawerv2.DBhelper.DBHelper
import com.example.virtualdrawerv2.Utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File

class Shoes : AppCompatActivity() {
    //variable declarations
    private val IMAGE_CAPTURE_CODE = 1001
    private val PERMISSION_CODE = 1000

    var bottomNavigationView: BottomNavigationView? =null
    var image_uri: Uri? =null
    var shoes_image: ImageView? = null
    var add_photo:Button? = null
    var random: Button? = null
    var previous_image: Button? = null
    var next_image: Button? = null
    var delete_button: Button? = null
    var current_id: Int = 1
    var total_images: TextView?= null
    var total: Int = 0
    var last_id: Int = 0

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    var nav_option1: String? = null

    var x1: Float = 0F
    var x2: Float = 0F
    var y1: Float = 0F
    var y2: Float = 0F

    internal lateinit var dbHelper : DBHelper

    //setting the navigation destinations
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_shirts -> {

                val intent = Intent(this, Tshirts::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hoodies -> {
                val intent = Intent(this, Hoodies::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfits -> {
                val intent = Intent(this, Outfits::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pants -> {
                val intent = Intent(this, Pants::class.java)
                startActivity(intent)
                overridePendingTransition(0,0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shoes -> {

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shoes)

        //set Preferences
        pref = applicationContext.getSharedPreferences("NavOpt", 0)
        editor = pref?.edit()


        //findViewByIds()
        add_photo = findViewById(R.id.add_new_photo)
        random = findViewById(R.id.random)
        shoes_image = findViewById(R.id.shoes_image)
        previous_image = findViewById(R.id.left_arrow)
        next_image = findViewById(R.id.right_arrow)
        total_images= findViewById(R.id.total_images)
        delete_button= findViewById(R.id.delete_button)
        bottomNavigationView= findViewById(R.id.bottom_navigation)


        //set home
        bottomNavigationView?.setSelectedItemId(R.id.navigation_shirts)
        bottomNavigationView?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        //set Database
        dbHelper = DBHelper(this)

        //set buttons transparency
        previous_image?.getBackground()?.setAlpha(40)
        next_image?.getBackground()?.setAlpha(40)
        delete_button?.getBackground()?.setAlpha(0)

        //show total number of images
        getTotalImages()

        //set first image to a random one
        setRandomImage()

        //choose new category icon and title
        bottomNavigationView?.findViewById<View>(R.id.navigation_shoes)?.setOnLongClickListener {

            chooseNavigationIcon("nav_option5",R.id.navigation_shoes)
            true
        }

        //when new image button is clicked
        add_photo?.setOnClickListener {

            val listItems = arrayOf("Camera", "Gallery")
            val mBuilder = AlertDialog.Builder(this@Shoes)
            mBuilder.setTitle("Add new image from")
            mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->

                if(i==0)
                {
                    //camera has been chosen
                    chooseImageFromCamera()
                }
                if(i==1)
                {
                    //gallery has been chosen
                    chooseImageFromGallery()
                }

                dialogInterface.dismiss()
            }
            // Set the neutral/cancel button click listener
            mBuilder.setNeutralButton("Cancel") { dialog, which ->
                // Do something when click the neutral button
                dialog.cancel()
            }

            val mDialog = mBuilder.create()
            mDialog.show()
        }

        //get Random image
        random?.setOnClickListener{
            setRandomImage()
        }


        //show previous image
        previous_image?.setOnClickListener()
        {
            setPreviousImage()
        }

        //show nect image
        next_image?.setOnClickListener()
        {
            setNextImage()
        }

        //delete image
        delete_button?.setOnClickListener()
        {
            deleteImage()

        }

        //active when user swipes across the ImageView
        shoes_image?.setOnTouchListener(View.OnTouchListener { view, event ->

            if (event != null) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        x1 = event.getX()
                        y1 = event.getY()
                    }
                    MotionEvent.ACTION_UP -> {

                        x2 = event.getX()
                        y2 = event.getY()

                        //swipe right
                        if (x1 < x2) {

                            setPreviousImage()

                        }

                        //swipe left
                        else if (x1 > x2) {

                            setNextImage()

                        }
                    }
                }
            }

            true
        })
    }

    //set navigation icon
    private fun setCustomNavigationIconFinal(nav_opt: String , item_id:Int) {
        nav_option1 = pref!!.getString(nav_opt, null)
        when (nav_option1) {
            "Shirts" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_tshirt)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Shirts"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Hoodies" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_hoodie)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Hoodies"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Pants" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_pants)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Pants"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Shoes" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_shoes)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Shoes"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Dresses" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_dress)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Dresses"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Hats" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_hat)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Hats"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Handbags" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_handbags)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Handbags"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Heels" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_heels)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Heels"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Watches" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_watch)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Watches"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
            "Jewelry" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_jewelry)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Jewelry"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }

        }
    }

    //match all icons
    private fun setCustomNavigationIcon() {
        if(pref?.getString("nav_option1", null) != null)
        {
            setCustomNavigationIconFinal("nav_option1",R.id.navigation_shirts)
        }
        if(pref?.getString("nav_option2", null) != null)
        {
            setCustomNavigationIconFinal("nav_option2",R.id.navigation_hoodies)
        }
        if(pref?.getString("nav_option3", null) != null)
        {
            setCustomNavigationIconFinal("nav_option3",R.id.navigation_outfits)
        }
        if(pref?.getString("nav_option4", null) != null)
        {
            setCustomNavigationIconFinal("nav_option4",R.id.navigation_pants)
        }
        if(pref?.getString("nav_option5", null) != null)
        {
            setCustomNavigationIconFinal("nav_option5",R.id.navigation_shoes)
        }
    }

    //display alert dialog and change icon and title
    private fun chooseNavigationIcon(nav_opt: String , item_id:Int) {
        var listItems = arrayOf("Tshirt", "Hoodies", "Pants", "Shoes", "Dresses", "Hats", "Handbags", "Heels", "Watches", "Jewelry")
        val mBuilder = AlertDialog.Builder(this@Shoes)
        mBuilder.setTitle("Change to")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->

            when (i) {
                //Shirts
                0 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Shirts")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_tshirt)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Shirts"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Hoodies
                1 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Hoodies")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_hoodie)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Hoodies"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Pants
                2 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Pants")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_pants)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Pants"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Shoes
                3 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Shoes")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_shoes)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Shoes"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Dresses
                4 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Dresses")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_dress)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Dresses"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Hats
                5 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Hats")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_hat)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Hats"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Handbags
                6 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Handbags")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_handbags)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Handbags"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Heels
                7 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Heels")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_heels)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Heels"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Watches
                8 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Watches")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_watch)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Watches"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Jewelry
                9 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Jewelry")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_jewelry)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Jewelry"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
            }



            dialogInterface.dismiss()
        }
        // Set the neutral/cancel button click listener
        mBuilder.setNeutralButton("Cancel") { dialog, which ->
            // Do something when click the neutral button
            dialog.cancel()
        }

        val mDialog = mBuilder.create()
        mDialog.show()
    }

    //active when user swipes across the screen
    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {

                    x1 = event.getX()
                    y1 = event.getY()
                }
                MotionEvent.ACTION_UP -> {

                    x2 = event.getX()
                    y2 = event.getY()

                    //swipe right
                    if (x1 < x2) {

                        setPreviousImage()

                    }

                    //swipe left
                    else if (x1 > x2) {

                        setNextImage()

                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    //show alert dialog and delete current image
    fun deleteImage()
    {
        val alertDialog = AlertDialog.Builder(this@Shoes)
        alertDialog.setTitle("Are you sure you want to delete this image?")

        alertDialog.setPositiveButton("Delete") { dialog, which ->
            dbHelper.deleteImage(current_id)
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
            getTotalImages()
            if(total==0)
            {

                shoes_image?.visibility = View.INVISIBLE
            }
            else
            {
                //show new image
                setRandomImage()
            }
        }
        alertDialog.setNegativeButton("Cancel") { dialog, which ->

        }
        alertDialog.show()
    }

    //set the imageView to the next image
    fun setNextImage()
    {
        if(total>1)
        {
            //check if the current image is the last one in the database
            var max = dbHelper.getLastID("Shoes")!!
            if (current_id >= max) {
                current_id = 0
            }

            //find the id of next image and display the image
            for (i in current_id + 1..max) {
                if (dbHelper.getBitmapByNameAndID("Shoes", i) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Shoes", i)!!)
                    shoes_image?.setImageBitmap(bitmap)
                    current_id = i
                    break
                }
            }
        }
    }

    //set the imageView to the previous image
    fun setPreviousImage()
    {
        if(total>1)
        {
            //check if the current image is the first one in the database
            var max = dbHelper.getLastID("Shoes")!!
            if (current_id <= 1) {
                current_id = max + 1
            }

            //find the id of the previous image and display the image
            for (i in current_id - 1 downTo 1) {
                if (dbHelper.getBitmapByNameAndID("Shoes", i) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Shoes", i)!!)
                    shoes_image?.setImageBitmap(bitmap)
                    current_id = i
                    break
                }
                if (i == 1) {
                    current_id = max
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Shoes", current_id)!!)
                    shoes_image?.setImageBitmap(bitmap)
                }
            }
        }
    }

    //set the imageView to a random image
    fun setRandomImage()
    {
        if(total>1)
        {
            //create random number smaller than last ID
            getLastID()

            var rand = (1..last_id).random()

            //create random number to match id from database and be different than current image
            while (dbHelper.getBitmapByNameAndID("Shoes", rand) == null || rand == current_id)
            {
                rand = (1..last_id).random()
            }

            //show random image
            if (dbHelper.getBitmapByNameAndID("Shoes", rand) != null)
            {
                val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Shoes", rand)!!)
                shoes_image?.setImageBitmap(bitmap)
                current_id = rand
            }
        }
        else if(total==1)
        {
            val bitmap = Utils.getImage(dbHelper.getBitmapByNameDesc("Shoes")!!)
            shoes_image?.setImageBitmap(bitmap)
            getLastID()
            current_id = last_id
        }
    }

    //add new image from camera
    fun chooseImageFromCamera()
    {
        //camera has been chosen
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                //permission not granted
                val permission = arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(permission, PERMISSION_CODE)
            }
            else
            {
                //permission granted
                openCamera()
            }
        }
        else
        {
            //os < marshmallow
            openCamera()
        }
    }

    //set the imageView to the next image
    fun chooseImageFromGallery()
    {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED)
            {
                //permission not granted
                val permission = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                requestPermissions(permission, PERMISSION_CODE)
            }
            else
            {
                //permission granted
                val photoPickerIntent = Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                photoPickerIntent.type = "image/*"
                photoPickerIntent.putExtra("crop", "true")
                photoPickerIntent.putExtra("outputX", 600)
                photoPickerIntent.putExtra("outputY", 800)
                photoPickerIntent.putExtra("aspectX", 3)
                photoPickerIntent.putExtra("aspectY", 4)
                photoPickerIntent.putExtra("scale", true)
                photoPickerIntent.putExtra(
                        "outputFormat",
                        Bitmap.CompressFormat.JPEG.toString()
                )
                startActivityForResult(photoPickerIntent, 1)
            }
        }
        else
        {
            //os < marshmallow
            val photoPickerIntent = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            )
            photoPickerIntent.type = "image/*"
            photoPickerIntent.putExtra("crop", "true")
            photoPickerIntent.putExtra("outputX", 600)
            photoPickerIntent.putExtra("outputY", 800)
            photoPickerIntent.putExtra("aspectX", 3)
            photoPickerIntent.putExtra("aspectY", 4)
            photoPickerIntent.putExtra("scale", true)
            photoPickerIntent.putExtra(
                    "outputFormat",
                    Bitmap.CompressFormat.JPEG.toString()
            )
            startActivityForResult(photoPickerIntent, 1)
        }

    }

    //get total number of images
    fun getTotalImages()
    {
        //get number and display it
        total = dbHelper.getTotalImages("Shoes")!!
        total_images?.setText(total.toString())

        //make navigation arrows and random button invisible
        if(total<2)
        {
            previous_image?.visibility = View.INVISIBLE
            next_image?.visibility = View.INVISIBLE
            random?.visibility = View.INVISIBLE
        }
        else
        //make navigation arrows and random button visible
        {
            previous_image?.visibility = View.VISIBLE
            next_image?.visibility = View.VISIBLE
            random?.visibility = View.VISIBLE
        }

        //make delete button invisible if no images
        if(total<1)
        {
            delete_button?.visibility = View.INVISIBLE
        }
        else
        {
            delete_button?.visibility = View.VISIBLE
        }
    }

    //get id of last image in the database
    fun getLastID()
    {
        last_id = dbHelper.getLastID("Shoes")!!
    }

    //use camera to take new picture
    private fun openCamera() {

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Outfits")
        values.put(MediaStore.Images.Media.ALBUM, "Orar")

        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE)
    }

    //check permissions result
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        when(requestCode)
        {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission was granted
                    //openCamera()
                } else {
                    //Permission was denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //set imageView to new image
    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK)
        {
            //if camera was used
            if(requestCode==IMAGE_CAPTURE_CODE) {
                //set Imageview to photo
                shoes_image?.setImageURI(image_uri)

                val bitmap = (shoes_image?.drawable as BitmapDrawable).bitmap

                DBHelper(applicationContext).addBitmap("Shoes", Utils.getBytes(bitmap))
                Toast.makeText(this@Shoes, "Saved!", Toast.LENGTH_SHORT).show()

                //set current_id to last entry
                getTotalImages()
                getLastID()
                current_id=last_id
            }

            //if new image from Gallery
            if(requestCode==1) {
                //set Imageview to photo
                val pickedImage = data?.data
                shoes_image?.setImageURI(pickedImage)

                val bitmap = (shoes_image?.drawable as BitmapDrawable).bitmap

                DBHelper(applicationContext).addBitmap("Shoes", Utils.getBytes(bitmap))
                Toast.makeText(this@Shoes, "Saved!", Toast.LENGTH_SHORT).show()

                //set current_id to last entry
                getTotalImages()
                getLastID()
                current_id=last_id
            }
        }
    }

    //go to Outfits when back button is pressed
    override fun onBackPressed() {
        val intent = Intent(this, Outfits::class.java)
        startActivity(intent)
        overridePendingTransition(0, 0)
        super.onBackPressed()

    }

    //show new random image on resume
    override fun onResume() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        //set home
        bottomNavigationView.setSelectedItemId(R.id.navigation_shoes)

        //set navigation icons and titles to custom
        setCustomNavigationIcon()

        getTotalImages()
        super.onResume()
    }


}