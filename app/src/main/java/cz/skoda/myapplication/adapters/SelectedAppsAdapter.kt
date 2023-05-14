package cz.skoda.myapplication.adapters

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import cz.skoda.myapplication.R
import cz.skoda.myapplication.interfaces.ItemTouchHelperViewHolder
import cz.skoda.myapplication.models.App
import cz.skoda.myapplication.models.SelectedApp
import java.util.*
import kotlin.collections.ArrayList

class SelectedAppsAdapter(private val context: Context, private val selectedAppList: ArrayList<SelectedApp>) : RecyclerView.Adapter<SelectedAppsAdapter.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_ITEM = 0
    }
    //private val apps = ArrayList<SelectedApp>()

//    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.app_item2, parent, false)
        return ViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val app = selectedAppList[position]
        holder.appNameTextView.text = app.appName.first().toString()
        holder.appDescTextView.text = app.appName

//        holder.itemView.setOnLongClickListener {
//            val dragShadow = View.DragShadowBuilder(holder.itemView)
//            holder.itemView.startDragAndDrop(null, dragShadow, null, 0)
//            true
//        }
    }

    override fun getItemCount(): Int {
        return selectedAppList.size
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(selectedAppList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(selectedAppList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        persistSelectedApps()
    }

    fun showMessage(appName:String, position:Int){
        Toast.makeText(context, "We will add "+appName+" at position "+position, Toast.LENGTH_LONG).show()
    }



    fun addApp(toPosition: Int, app: App) {
        val app = SelectedApp(app.appName)
        if (selectedAppList.size > 0) {
            if(toPosition <= selectedAppList.size-1){
                selectedAppList.add(toPosition, app)
            }else{
                selectedAppList.add(0, app)
            }

        } else {
            // handle the case when the list is empty
            selectedAppList.add(0, app)
        }

        notifyItemInserted(toPosition)
        notifyDataSetChanged()

        Toast.makeText(context, ""+selectedAppList.size, Toast.LENGTH_LONG).show()

        persistSelectedApps()
    }

    fun getItem(position: Int): SelectedApp {


//        return if (position in 0 until selectedAppList.size) {
//            selectedAppList[position]
//        } else {
//            null
//        }

        return  selectedAppList[position]

    }

    fun updateList(newAppList: ArrayList<SelectedApp>) {
        selectedAppList.clear()
        selectedAppList.addAll(newAppList)
        notifyDataSetChanged()
        //Log.d("CHECK", "Hello"+newAppList[0].appName)
    }





    fun removeItem(position: Int): SelectedApp {
//        var appInfo: SelectedApp
//        if(position > 0) {
//            appInfo = selectedAppList.removeAt(position)
//            notifyItemRemoved(position)
//            Log.d("APPREM", "" + selectedAppList.size)
//            //persistApps()
//        }
//
//        return appInfo

//        var appInfo: SelectedApp? = null
//        if (position in 0 until selectedAppList.size) {
//            appInfo = selectedAppList.removeAt(position)
//            notifyItemRemoved(position)
//            Log.d("APPREM", "" + selectedAppList.size)
//            persistSelectedApps()
//        }
//        return appInfo

        val appInfo = selectedAppList.removeAt(position)
        notifyItemRemoved(position)
        Log.d("APPREM", ""+selectedAppList.size)
        persistSelectedApps()
        return appInfo

    }

    private fun persistSelectedApps(){
        val selectedAppsListString = Gson().toJson(selectedAppList)

        val sharedPreferences = context.getSharedPreferences("SelectedAppsPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putString("selectedAppsOrder", selectedAppsListString)

        // Apply the changes
        editor.apply()

        Toast.makeText(context, "WE updated the presfs", Toast.LENGTH_LONG).show()
    }




    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        val appNameTextView: TextView = itemView.findViewById(R.id.appName)
        val appDescTextView: TextView = itemView.findViewById(R.id.app_des)

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }
}
