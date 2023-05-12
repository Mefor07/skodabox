package cz.skoda.myapplication.adapters


import android.content.ClipData
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import cz.skoda.myapplication.MainActivity
import cz.skoda.myapplication.R
import cz.skoda.myapplication.interfaces.ItemTouchHelperAdapter
import cz.skoda.myapplication.interfaces.ItemTouchHelperViewHolder
import cz.skoda.myapplication.models.App
import cz.skoda.myapplication.models.SelectedApp
import java.util.*
import kotlin.collections.ArrayList


class InstallAppsAdapter(private val context: MainActivity, private val appList: ArrayList<App>) :
    RecyclerView.Adapter<InstallAppsAdapter.ViewHolder>(){

    companion object {
        const val VIEW_TYPE_ITEM = 1
    }



    fun removeItem(position: Int): App {
        val appInfo = appList.removeAt(position)
        notifyItemRemoved(position)
        Log.d("APPREM", ""+appList.size)
        persistApps()
        return appInfo
    }



    fun addApp(toPosition: Int, app: SelectedApp) {
        val app = App(app.appName)
        if (appList.size > 0) {
            if(toPosition <= appList.size-1){
                appList.add(toPosition, app)
            }else{
                appList.add(0, app)
            }

        } else {
            // handle the case when the list is empty
            appList.add(0, app)
        }
        //apps.add(toPosition, app)
        notifyItemInserted(toPosition)
        notifyDataSetChanged()

        //Toast.makeText(context, "we want to add"+app.appName, Toast.LENGTH_LONG).show()

        persistApps()

    }

    fun getItem(position: Int): App {
        return appList[position]
    }

    fun updateList(newAppList: ArrayList<App>) {
        appList.clear()
        appList.addAll(newAppList)
        notifyDataSetChanged()
        Log.d("CHECK", "Hello"+newAppList[0].appName)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val appInfo = appList[position]
        //holder.appIcon.setImageDrawable(appInfo.loadIcon(mPackageManager))
        holder.appName.text = appInfo.appName.first().toString()
        holder.appDesc.text = appInfo.appName





        if (context is MainActivity) {
            val mainActivity = context
            // use mainActivity instance
            //mainActivity.appListRecyclerView2.setOnDragListener(this)
        }


    }

    override fun getItemCount(): Int {
        return appList.size
    }




    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(appList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(appList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        Log.d("POS",""+appList[0].appName)

        persistApps()
    }



    private fun persistApps(){

        val appsListString = Gson().toJson(appList)

        val sharedPreferences = context.getSharedPreferences("AppsPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putString("appsListOrder", appsListString)

        // Apply the changes
        editor.apply()

    }

    fun onItemDismiss(position: Int) {
        appList.removeAt(position)
        notifyItemRemoved(position)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        var appName: TextView = itemView.findViewById(R.id.appName)
        var appDesc: TextView = itemView.findViewById(R.id.app_des)

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }


    fun showMsg(){
        Toast.makeText(context, "Show message", Toast.LENGTH_LONG).show()
    }
}
