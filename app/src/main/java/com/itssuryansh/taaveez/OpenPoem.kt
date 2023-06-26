package com.itssuryansh.taaveez

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.util.Log
import android.widget.PopupMenu
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.itssuryansh.taaveez.databinding.AboutOfOpenContentBinding
import com.itssuryansh.taaveez.databinding.ActivityOpenPoemBinding

class OpenPoem : AppCompatActivity() {

    private var binding: ActivityOpenPoemBinding? = null
    private var poemTopic: String? = null
    private var poemDes: String? = null
    private var createdDate: String? = null
    private var updatedDate: String? = null
    private var textToCopy: String? = null
    private var id: Int? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        loadDayNight()
        poemTopic = intent.getStringExtra(Constants.POEM_TOPIC)
        poemDes = intent.getStringExtra(Constants.POEM_DES)
        createdDate = intent.getStringExtra(Constants.CREATED_DATE)
        updatedDate = intent.getStringExtra(Constants.UPDATED_DATE)
        id = intent.getIntExtra(Constants.ID, 0)

        super.onCreate(savedInstanceState)

        binding = ActivityOpenPoemBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnMenu?.setOnClickListener {
            popup()
        }

        binding?.tvTopic?.text = poemTopic
        binding?.tvPoemDes?.text = Html.fromHtml(poemDes)

        binding?.tvPoemDes?.movementMethod = LinkMovementMethod.getInstance() // enable link clicking
        binding?.tvPoemDes?.setTextIsSelectable(true) // enable text selection

        textToCopy = Html.fromHtml(poemDes).toString()
    }

    private fun popup() {
        val popupMenu = PopupMenu(this, binding?.btnMenu)
        popupMenu.inflate(R.menu.menu)

// // Set the background color of the PopupMenu
//        val popupMenuHelper = MenuPopupHelper(this, popupMenu.menu as MenuBuilder, binding?.btnMenu)
//        popupMenuHelper.setGravity(Gravity.BOTTOM)
//        popupMenuHelper.setBgColor(ContextCompat.getColor(this, R.color.popup_menu_background))
//        popupMenuHelper.show()

        popupMenu.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit_data -> {
                    val intent = Intent(this@OpenPoem, EditContent::class.java)
                    intent.putExtra(Constants.ID, id)
                    startActivity(intent)
                    true
                }

                R.id.copy_data -> {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("label", textToCopy)
                    clipboard.setPrimaryClip(clip)
                    true
                }
                R.id.share_data -> {
                    shareContent()
                    true
                }
                R.id.about_data -> {
                    // Handle about data action
                    dialogAboutOpenContent()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popupMenu)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            Log.e("Main", "Error showing menu icons.", e)
        } finally {
            popupMenu.show()
        }
    }

    private fun shareContent() {
        val sendIntent = Intent()
        sendIntent.type = "text/plain"
        sendIntent.action = Intent.ACTION_SEND
        val body = "Topic = ${poemTopic}\n" +
            "--------------------------------\n" +
            "${Html.fromHtml(poemDes)}"
        sendIntent.putExtra(Intent.EXTRA_TEXT, body)
        Intent.createChooser(sendIntent, "Share using")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(sendIntent)
    }

    private fun dialogAboutOpenContent() {
        val aboutDialog = Dialog(this)
        aboutDialog.setCancelable(false)
        val binding = AboutOfOpenContentBinding.inflate(layoutInflater)
        aboutDialog.setContentView(binding.root)

        binding?.tvPoemCreatedDate?.text = createdDate
        binding?.tvPoemUpdatedDate?.text = updatedDate

        binding?.btnAboutOpenContentBack?.setOnClickListener {
            aboutDialog.dismiss()
        }
        aboutDialog.show()
    }

    override fun onBackPressed() {
        openNotesActivity()
    }

    fun openNotesActivity() {
        val intent = Intent(this@OpenPoem, Notes::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.drawable.slide_in_left, R.drawable.slide_out_rigth)
        finish()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun loadDayNight() {
        val sharedPreferences = getSharedPreferences("DayNight", Activity.MODE_PRIVATE)
        val dayNight = sharedPreferences.getString("My_DayNight", "MyDayNight")
        if (dayNight != null) {
            setDayNight(dayNight)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun setDayNight(daynightMode: String) {
        val editor = getSharedPreferences("DayNight", Context.MODE_PRIVATE).edit()
        editor.putString("My_DayNight", daynightMode)
        editor.apply()
        if (daynightMode == "yes") {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
