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
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.virtualdrawerv2.DBhelper.DBHelper
import com.example.virtualdrawerv2.Utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.IOException

class Outfits : AppCompatActivity() {
    //variable declarations
    //Tshirts
    var tshirt_card: CardView? = null
    var tshirt_image: ImageView? = null
    var tshirt_long_check: Int = 0
    var tshirt_current_id: Int = 1
    var tshirt_total: Int = 0
    var tshirt_last_id: Int = 0
    //Hoodies
    var hoodie_card: CardView? = null
    var hoodie_image: ImageView? = null
    var hoodie_long_check: Int = 0
    var hoodie_current_id: Int = 1
    var hoodie_total: Int = 0
    var hoodie_last_id: Int = 0
    //Pants
    var pants_card: CardView? = null
    var pants_image: ImageView? = null
    var pants_long_check: Int = 0
    var pants_current_id: Int = 1
    var pants_total: Int = 0
    var pants_last_id: Int = 0
    //Shoes
    var shoes_card: CardView? = null
    var shoes_image: ImageView? = null
    var shoes_long_check: Int = 0
    var shoes_current_id: Int = 1
    var shoes_total: Int = 0
    var shoes_last_id: Int = 0

    var random: Button? = null

    //Alternative View
    private val IMAGE_CAPTURE_CODE = 1001
    private val PERMISSION_CODE = 1000

    var image_uri: Uri? =null
    var alt_card: CardView? = null
    var alt_image: ImageView? = null
    var alt_random: Button? = null
    var add_photo: Button? = null
    var previous_image: Button? = null
    var next_image: Button? = null
    var delete_button: Button? = null
    var current_id: Int = 1
    var total_images: TextView?= null
    var total: Int = 0
    var last_id: Int = 0

    var bottomNavigationView: BottomNavigationView? =null

    var pref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    var nav_option1: String? = null

    var x1: Float = 0F
    var x2: Float = 0F
    var y1: Float = 0F
    var y2: Float = 0F


    internal lateinit var dbHelper :DBHelper

    //set navigaion listener
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_shirts -> {

                val intent = Intent(this, Tshirts::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_hoodies -> {
                val intent = Intent(this, Hoodies::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_outfits -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_pants -> {
                val intent = Intent(this, Pants::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_shoes -> {
                val intent = Intent(this, Shoes::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outfits)

        //set Preferences
        pref = applicationContext.getSharedPreferences("NavOpt", 0)
        editor = pref?.edit()

        //findViewByIds()
        random = findViewById(R.id.random_outfit)
        tshirt_image = findViewById(R.id.tshirt_image)
        hoodie_image = findViewById(R.id.hoodie_image)
        pants_image = findViewById(R.id.pants_image)
        shoes_image = findViewById(R.id.shoes_image)

        tshirt_card = findViewById(R.id.tshirt_cardView)
        hoodie_card = findViewById(R.id.hoodie_cardView)
        pants_card = findViewById(R.id.pants_cardView)
        shoes_card = findViewById(R.id.shoes_cardView)

        //findViewByIds2()
        add_photo = findViewById(R.id.add_new_photo)
        alt_random = findViewById(R.id.alt_random)
        alt_card= findViewById(R.id.alt_cardView)
        alt_image = findViewById(R.id.alt_image)
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

        //choose new category icon and title
        bottomNavigationView?.findViewById<View>(R.id.navigation_outfits)?.setOnLongClickListener {

            chooseNavigationIcon("nav_option3", R.id.navigation_outfits)
            true
        }

        //set navigation icons and titles to custom
        setCustomNavigationIcon()

        //check if in Outfits mode
        if(bottomNavigationView?.menu?.findItem(R.id.navigation_outfits)?.title == "Outfits")
        {
            outfit_mode()
        }
        else
        {
            image_mode()
        }

    }

    //Image Mode
    @SuppressLint("ClickableViewAccessibility")
    fun image_mode()
    {
        //change screen layout
        tshirt_card?.visibility = View.INVISIBLE
        hoodie_card?.visibility = View.INVISIBLE
        pants_card?.visibility = View.INVISIBLE
        shoes_card?.visibility = View.INVISIBLE
        random?.visibility = View.INVISIBLE

        add_photo?.visibility = View.VISIBLE
        alt_random?.visibility = View.VISIBLE
        alt_card?.visibility = View.VISIBLE
        alt_image?.visibility = View.VISIBLE
        previous_image?.visibility = View.VISIBLE
        next_image?.visibility = View.VISIBLE
        total_images?.visibility = View.VISIBLE
        delete_button?.visibility = View.VISIBLE

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

        //when new image button is clicked
        add_photo?.setOnClickListener {

            val listItems = arrayOf("Camera", "Gallery")
            val mBuilder = AlertDialog.Builder(this@Outfits)
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
        alt_random?.setOnClickListener{
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
        alt_image?.setOnTouchListener(View.OnTouchListener { view, event ->

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

    //Outfits Mode
    fun outfit_mode()
    {
        //change screen layout
        tshirt_card?.visibility = View.VISIBLE
        hoodie_card?.visibility = View.VISIBLE
        pants_card?.visibility = View.VISIBLE
        shoes_card?.visibility = View.VISIBLE
        random?.visibility = View.VISIBLE

        add_photo?.visibility = View.INVISIBLE
        alt_random?.visibility = View.INVISIBLE
        alt_card?.visibility = View.INVISIBLE
        alt_image?.visibility = View.INVISIBLE
        previous_image?.visibility = View.INVISIBLE
        next_image?.visibility = View.INVISIBLE
        total_images?.visibility = View.INVISIBLE
        delete_button?.visibility = View.INVISIBLE


        //get total number of images
        getTotalImagesTshirt()
        getTotalImagesHoodie()
        getTotalImagesPants()
        getTotalImagesShoes()

        //set first image to a random one
        setRandomImageTshirt()
        setRandomImageHoodie()
        setRandomImagePants()
        setRandomImageShoes()

        //make  random button appear/dissapear
        checkRandomButton()

        //get Random images
        random?.setOnClickListener{
            setRandomImageTshirt()
            setRandomImageHoodie()
            setRandomImagePants()
            setRandomImageShoes()
        }



        //T-shirts image on click
        tshirt_image?.setOnClickListener()
        {
            if(tshirt_total==0)
            {
                val intent = Intent(this, Tshirts::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            else if(tshirt_total>1)
            {
                setRandomImageTshirt()
            }
        }

        //Hoodies image on click
        hoodie_image?.setOnClickListener()
        {
            if(hoodie_total==0)
            {
                val intent = Intent(this, Hoodies::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            else if(hoodie_total>1)
            {
                setRandomImageHoodie()
            }
        }

        //Pants image on click
        pants_image?.setOnClickListener()
        {
            if(pants_total==0)
            {
                val intent = Intent(this, Pants::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            else if(pants_total>1)
            {
                setRandomImagePants()
            }
        }

        //Shoes image on click
        shoes_image?.setOnClickListener()
        {
            if(shoes_total==0)
            {
                val intent = Intent(this, Shoes::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            else if(shoes_total>1)
            {
                setRandomImageShoes()
            }
        }

        //Tshirt image on Long click
        tshirt_image?.setOnLongClickListener()
        {
            if(tshirt_total>=2)
            {
                //act as a switch button
                if (tshirt_long_check == 0)
                {
                    //select image
                    tshirt_image?.setBackgroundColor(0xFF006400.toInt())
                    tshirt_image?.setPadding(15, 15, 15, 15)
                    tshirt_long_check = 1
                }
                else if (tshirt_long_check == 1)
                {
                    //set image back to normal
                    tshirt_image?.setBackgroundColor(0xFF424242.toInt())
                    tshirt_image?.setPadding(0, 0, 0, 0)
                    tshirt_long_check = 0
                }
            }
            return@setOnLongClickListener true
        }

        //Hoodies image on Long click
        hoodie_image?.setOnLongClickListener()
        {
            if (hoodie_total >= 2)
            {
                //act as a switch button
                if (hoodie_long_check == 0)
                {
                    //select image
                    hoodie_image?.setBackgroundColor(0xFF006400.toInt())
                    hoodie_image?.setPadding(15, 15, 15, 15)
                    hoodie_long_check = 1
                }
                else if (hoodie_long_check == 1)
                {
                    //set image back to normal
                    hoodie_image?.setBackgroundColor(0xFF424242.toInt())
                    hoodie_image?.setPadding(0, 0, 0, 0)
                    hoodie_long_check = 0
                }
            }
            return@setOnLongClickListener true
        }
        //Pants image on Long click
        pants_image?.setOnLongClickListener()
        {
            if(pants_total>=2)
            {
                //act as a switch button
                if (pants_long_check == 0)
                {
                    //select image
                    pants_image?.setBackgroundColor(0xFF006400.toInt())
                    pants_image?.setPadding(15, 15, 15, 15)
                    pants_long_check = 1
                }
                else if (pants_long_check == 1)
                {
                    //set image back to normal
                    pants_image?.setBackgroundColor(0xFF424242.toInt())
                    pants_image?.setPadding(0, 0, 0, 0)
                    pants_long_check = 0
                }
            }
            return@setOnLongClickListener true
        }
        //Shoes image on Long click
        shoes_image?.setOnLongClickListener()
        {   if(shoes_total>=2)
        {
            //act as a switch button
            if (shoes_long_check == 0)
            {
                //select image
                shoes_image?.setBackgroundColor(0xFF006400.toInt())
                shoes_image?.setPadding(15, 15, 15, 15)
                shoes_long_check = 1
            }
            else if (shoes_long_check == 1)
            {
                //set image back to normal
                shoes_image?.setBackgroundColor(0xFF424242.toInt())
                shoes_image?.setPadding(0, 0, 0, 0)
                shoes_long_check = 0
            }
        }
            return@setOnLongClickListener true
        }
    }

    /////////////////////////////////////////IMAGE MODE FUNCTIONS/////////////////////////////////////////////////

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
        val alertDialog = AlertDialog.Builder(this@Outfits)
        alertDialog.setTitle("Are you sure you want to delete this image?")

        alertDialog.setPositiveButton("Delete") { dialog, which ->
            dbHelper.deleteImage(current_id)
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
            getTotalImages()
            if(total==0)
            {
                alt_image?.visibility = View.INVISIBLE

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
            var max = dbHelper.getLastID("Alt")!!
            if (current_id >= max) {
                current_id = 0
            }

            //find the id of next image and display the image
            for (i in current_id + 1..max) {
                if (dbHelper.getBitmapByNameAndID("Alt", i) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Alt", i)!!)

                    alt_image?.setImageBitmap(bitmap)

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
            var max = dbHelper.getLastID("Alt")!!
            if (current_id <= 1) {
                current_id = max + 1
            }

            //find the id of the previous image and display the image
            for (i in current_id - 1 downTo 1) {
                if (dbHelper.getBitmapByNameAndID("Alt", i) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Alt", i)!!)
                    alt_image?.setImageBitmap(bitmap)
                    current_id = i
                    break
                }
                if (i == 1) {
                    current_id = max
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Alt", current_id)!!)
                    alt_image?.setImageBitmap(bitmap)
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
            while (dbHelper.getBitmapByNameAndID("Alt", rand) == null || rand==current_id)
            {
                rand = (1..last_id).random()
            }

            //show random image
            if (dbHelper.getBitmapByNameAndID("Alt", rand) != null)
            {
                val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Alt", rand)!!)
                alt_image?.setImageBitmap(bitmap)
                current_id = rand
            }
        }
        else if(total==1)
        {
            val bitmap = Utils.getImage(dbHelper.getBitmapByNameDesc("Alt")!!)
            alt_image?.setImageBitmap(bitmap)

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


    //add new image from gallery
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
        total = dbHelper.getTotalImages("Alt")!!
        total_images?.setText(total.toString())

        //make navigation arrows and random button invisible
        if(total<2)
        {
            previous_image?.visibility = View.INVISIBLE
            next_image?.visibility = View.INVISIBLE
            alt_random?.visibility = View.INVISIBLE
        }
        else
        //make navigation arrows and random button visible
        {
            previous_image?.visibility = View.VISIBLE
            next_image?.visibility = View.VISIBLE
            alt_random?.visibility = View.VISIBLE
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
        last_id = dbHelper.getLastID("Alt")!!
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
                alt_image?.setImageURI(image_uri)

                val bitmap = (alt_image?.drawable as BitmapDrawable).bitmap

                DBHelper(applicationContext).addBitmap("Alt", Utils.getBytes(bitmap))
                Toast.makeText(this@Outfits, "Saved!", Toast.LENGTH_SHORT).show()

                //set current_id to last entry
                getLastID()
                current_id=last_id
            }

            //if new image from Gallery
            if(requestCode==1) {
                //set Imageview to photo
                val pickedImage = data?.data
                alt_image?.setImageURI(pickedImage)

                val bitmap = (alt_image?.drawable as BitmapDrawable).bitmap

                DBHelper(applicationContext).addBitmap("Alt", Utils.getBytes(bitmap))
                Toast.makeText(this@Outfits, "Saved!", Toast.LENGTH_SHORT).show()

                //set current_id to last entry
                getLastID()
                current_id=last_id
            }
        }
    }

    /////////////////////////////////////////IMAGE MODE FUNCTIONS/////////////////////////////////////////////////

    /////////////////////////////////////////OUTFITS MODE FUNCTIONS/////////////////////////////////////////////////


    //set navigation icon
    private fun setCustomNavigationIconFinal(nav_opt: String , item_id:Int) {
        nav_option1 = pref!!.getString(nav_opt, null)
        when (nav_option1) {
            "Outfits" -> {
                var myDrawable = getResources().getDrawable(R.drawable.ic_outfit)
                bottomNavigationView?.menu?.findItem(item_id)?.title = "Outfits"
                bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
            }
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
            setCustomNavigationIconFinal("nav_option1", R.id.navigation_shirts)
        }
        if(pref?.getString("nav_option2", null) != null)
        {
            setCustomNavigationIconFinal("nav_option2", R.id.navigation_hoodies)
        }
        if(pref?.getString("nav_option3", null) != null)
        {
            setCustomNavigationIconFinal("nav_option3", R.id.navigation_outfits)
        }
        if(pref?.getString("nav_option4", null) != null)
        {
            setCustomNavigationIconFinal("nav_option4", R.id.navigation_pants)
        }
        if(pref?.getString("nav_option5", null) != null)
        {
            setCustomNavigationIconFinal("nav_option5", R.id.navigation_shoes)
        }
    }

    //display alert dialog and change icon and title
    private fun chooseNavigationIcon(nav_opt: String , item_id:Int) {
        var listItems = arrayOf("Outfits", "Tshirt", "Hoodies", "Pants", "Shoes", "Dresses", "Hats", "Handbags", "Heels", "Watches", "Jewelry")
        val mBuilder = AlertDialog.Builder(this@Outfits)
        mBuilder.setTitle("Change to")
        mBuilder.setSingleChoiceItems(listItems, -1) { dialogInterface, i ->

            when (i) {
                //Outfits
                0 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Outfits")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_outfit)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Outfits"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Shirts
                1 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Shirts")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_tshirt)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Shirts"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Hoodies
                2 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Hoodies")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_hoodie)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Hoodies"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Pants
                3 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Pants")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_pants)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Pants"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Shoes
                4 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Shoes")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_shoes)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Shoes"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Dresses
                5 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Dresses")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_dress)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Dresses"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Hats
                6 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Hats")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_hat)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Hats"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Handbags
                7 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Handbags")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_handbags)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Handbags"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Heels
                8 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Heels")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_heels)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Heels"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Watches
                9 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Watches")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_watch)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Watches"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
                //Jewelry
                10 -> {
                    // Storing string
                    editor?.putString(nav_opt, "Jewelry")

                    // commit changes
                    editor?.apply()

                    var myDrawable = getResources().getDrawable(R.drawable.ic_jewelry)
                    bottomNavigationView?.menu?.findItem(item_id)?.title = "Jewelry"
                    bottomNavigationView?.menu?.findItem(item_id)?.icon = myDrawable
                }
            }

            //check if Outfits mode is on
            if(bottomNavigationView?.menu?.findItem(R.id.navigation_outfits)?.title == "Outfits")
            {
                outfit_mode()
            }
            else
            {
                image_mode()
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

    //set the Tshirt imageView to a random image
    fun setRandomImageTshirt()
    {
        //check if image has been selected
        if(tshirt_long_check==0)
        {
            getTotalImagesTshirt()
            if (tshirt_total > 1)
            {
                //create random number smaller than last ID
                getLastIDTshirt()
                var rand = (1..tshirt_last_id).random()

                //create random number to match id from database and be different than current image
                while (dbHelper.getBitmapByNameAndID("Tshirt", rand) == null || rand == tshirt_current_id) {
                    rand = (1..tshirt_last_id).random()
                }

                //show random image
                if (dbHelper.getBitmapByNameAndID("Tshirt", rand) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Tshirt", rand)!!)
                    tshirt_image?.setImageBitmap(bitmap)
                    tshirt_current_id = rand
                }
            }
            else if (tshirt_total == 1)
            {
                val bitmap = Utils.getImage(dbHelper.getBitmapByNameDesc("Tshirt")!!)
                tshirt_image?.setImageBitmap(bitmap)

                getLastIDTshirt()
                tshirt_current_id = tshirt_last_id
            }
        }
    }

    //set the Hoodie imageView to a random image
    fun setRandomImageHoodie()
    {
        //check if image has been selected
        if(hoodie_long_check==0)
        {
            getTotalImagesHoodie()
            if (hoodie_total > 1)
            {
                //create random number smaller than last ID
                getLastIDHoodie()
                var rand = (1..hoodie_last_id).random()

                //create random number to match id from database and be different than current image
                while (dbHelper.getBitmapByNameAndID("Hoodies", rand) == null || rand == hoodie_current_id) {
                    rand = (1..hoodie_last_id).random()
                }

                //show random image
                if (dbHelper.getBitmapByNameAndID("Hoodies", rand) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Hoodies", rand)!!)
                    hoodie_image?.setImageBitmap(bitmap)
                    hoodie_current_id = rand
                }
            }
            else if (hoodie_total == 1)
            {
                val bitmap = Utils.getImage(dbHelper.getBitmapByNameDesc("Hoodies")!!)
                hoodie_image?.setImageBitmap(bitmap)

                getLastIDHoodie()
                hoodie_current_id = hoodie_last_id
            }
        }

    }

    //set the Pants imageView to a random image
    fun setRandomImagePants()
    {
        //check if image has been selected
        if(pants_long_check==0)
        {
            getTotalImagesPants()
            if (pants_total > 1)
            {
                //create random number smaller than last ID
                getLastIDPants()
                var rand = (1..pants_last_id).random()

                //create random number to match id from database and be different than current image
                while (dbHelper.getBitmapByNameAndID("Pants", rand) == null || rand == pants_current_id) {
                    rand = (1..pants_last_id).random()
                }

                //show random image
                if (dbHelper.getBitmapByNameAndID("Pants", rand) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Pants", rand)!!)
                    pants_image?.setImageBitmap(bitmap)
                    pants_current_id = rand
                }
            }
            else if (pants_total == 1)
            {
                val bitmap = Utils.getImage(dbHelper.getBitmapByNameDesc("Pants")!!)
                pants_image?.setImageBitmap(bitmap)

                getLastIDPants()
                pants_current_id = pants_last_id
            }
        }
    }

    //set the Shoes imageView to a random image
    fun setRandomImageShoes()
    {
        //check if image has been selected
        if(shoes_long_check==0)
        {
            getTotalImagesShoes()
            if (shoes_total > 1)
            {
                //create random number smaller than last ID
                getLastIDShoes()
                var rand = (1..shoes_last_id).random()

                //create random number to match id from database and be different than current image
                while (dbHelper.getBitmapByNameAndID("Shoes", rand) == null || rand == shoes_current_id) {
                    rand = (1..shoes_last_id).random()
                }

                //show random image
                if (dbHelper.getBitmapByNameAndID("Shoes", rand) != null) {
                    val bitmap = Utils.getImage(dbHelper.getBitmapByNameAndID("Shoes", rand)!!)
                    shoes_image?.setImageBitmap(bitmap)
                    shoes_current_id = rand
                }
            }
            else if (shoes_total == 1)
            {
                val bitmap = Utils.getImage(dbHelper.getBitmapByNameDesc("Shoes")!!)
                shoes_image?.setImageBitmap(bitmap)

                getLastIDShoes()
                shoes_current_id = shoes_last_id
            }
        }
    }

    // get total number of images in database
    fun getTotalImagesTshirt()
    {
        tshirt_total = dbHelper.getTotalImages("Tshirt")!!
    }
    fun getTotalImagesHoodie()
    {
        hoodie_total = dbHelper.getTotalImages("Hoodies")!!
    }
    fun getTotalImagesPants()
    {
        pants_total = dbHelper.getTotalImages("Pants")!!
    }
    fun getTotalImagesShoes()
    {
        shoes_total = dbHelper.getTotalImages("Shoes")!!
    }

    //get the id of last image in database
    fun getLastIDTshirt()
    {
        tshirt_last_id = dbHelper.getLastID("Tshirt")!!
    }
    fun getLastIDHoodie()
    {
        hoodie_last_id = dbHelper.getLastID("Hoodies")!!
    }
    fun getLastIDPants()
    {
        pants_last_id = dbHelper.getLastID("Pants")!!
    }
    fun getLastIDShoes()
    {
        shoes_last_id = dbHelper.getLastID("Shoes")!!
    }

    //make button visible/invisible depending on the number of images available
    fun checkRandomButton()
    {
        getTotalImagesTshirt()
        getTotalImagesHoodie()
        getTotalImagesPants()
        getTotalImagesShoes()

        //check if outfit mode is on
        if(bottomNavigationView?.menu?.findItem(R.id.navigation_outfits)?.title == "Outfits")
        {
            if(tshirt_total>=2||hoodie_total>=2||pants_total>=2||shoes_total>=2)
            {
                random?.visibility = View.VISIBLE
            }
            else
            {
                random?.visibility = View.INVISIBLE
            }
        }


    }


    //set new random image on resume
    override fun onResume() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        //set home
        bottomNavigationView.setSelectedItemId(R.id.navigation_outfits)

        //set navigation icons and titles to custom
        setCustomNavigationIcon()

        //test check page icon ----WORKS-----
        if(bottomNavigationView?.menu?.findItem(R.id.navigation_outfits)?.title == "Outfits")
        {

                //set first image to a random one
                setRandomImageTshirt()

                //set first image to a random one
                setRandomImageHoodie()


                //set first image to a random one
                setRandomImagePants()


                //set first image to a random one
                setRandomImageShoes()


                //make  random button appear/dissapear
                checkRandomButton()

        }
        else
        {
            getTotalImages()
        }
        
        super.onResume()
    }
}