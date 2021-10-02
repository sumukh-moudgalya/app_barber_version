package com.defines.bloomerbarber


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


private val TAG="LoadingActivity"

class LoadingActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_loading)


        val app_logo: ImageView = findViewById(R.id.loading_activity_logo)
        val app_name: TextView=findViewById(R.id.loading_activity_app_name)
        val app_company:TextView=findViewById(R.id.loading_activity_intiative_tag_line)


        val topAnim: Animation = AnimationUtils.loadAnimation(this,R.anim.top_anim)
        val bottomAnim: Animation = AnimationUtils.loadAnimation(this,R.anim.bottom_animation)



        app_logo.animation=topAnim
        app_name.animation=bottomAnim
        app_company.animation=bottomAnim

        auth=FirebaseAuth.getInstance()
        val user=auth.currentUser

        if (user!=null){
            val uid=user.uid
            val ref=FirebaseDatabase.getInstance().getReference("/barber/$uid/shopDetailsUploaded")
            ref.get().addOnSuccessListener{
                val isShop=it.value as Boolean
                if (isShop){
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent= Intent(this,MainActivity::class.java)
                        startActivity(intent)
                    },2000)
                }else{
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent= Intent(this,ShopAdderActivity::class.java)
                        startActivity(intent)
                    },2000)
                }
            }


        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                val intent= Intent(this,LoginActivity::class.java)
                startActivity(intent)
            },2000)
        }


    }
}