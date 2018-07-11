package project.example.arcoreplanerenderer

import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.plane_button).setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, PlaneRendererActivity::class.java))
        })

        findViewById<Button>(R.id.anchor_button).setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, AnchorActivity::class.java))
        })
    }
}
