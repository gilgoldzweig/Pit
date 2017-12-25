package goldzweigapps.com.pit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.AppCompatButton
import goldzweigapps.com.pit.view.Pit

class MainActivity : AppCompatActivity() {
    private lateinit var pitCustomView: Pit
    private lateinit var insertDotButton: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //inflate the views
        pitCustomView = findViewById(R.id.main_activity_pit_view)
        insertDotButton = findViewById(R.id.main_activity_insert_dot_button)

        //add dot button clicked
        insertDotButton.setOnClickListener {
           pitCustomView.insert()
        }
    }
}
