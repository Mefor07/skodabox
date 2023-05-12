package cz.skoda.myapplication

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import cz.skoda.myapplication.adapters.InstallAppsAdapter
import cz.skoda.myapplication.adapters.SelectedAppsAdapter
import cz.skoda.myapplication.adapters.SelectedAppsAdapter.Companion.VIEW_TYPE_ITEM
import cz.skoda.myapplication.models.App
import cz.skoda.myapplication.models.SelectedApp
import java.lang.Math.abs


class MainActivity : AppCompatActivity(){


    lateinit var appListRecyclerView: RecyclerView
    lateinit var appListRecyclerView2: RecyclerView

    lateinit var adapter1 :InstallAppsAdapter
    lateinit var adapter2: SelectedAppsAdapter
    val appsList = ArrayList<App>()
    var refinedAppList = ArrayList<App>()
    var selectedAppList = ArrayList<SelectedApp>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        adapter1 = InstallAppsAdapter(this, refinedAppList);
        adapter2 = SelectedAppsAdapter(this, selectedAppList)



        setContentView(R.layout.activity_main)
        var rootView = window.decorView.rootView

        appListRecyclerView = findViewById(R.id.installedAppsRecyclerView)
        appListRecyclerView2 = findViewById(R.id.selectedAppsRecyclerView2)
        appListRecyclerView.layoutManager = GridLayoutManager(this, 4)

        adapter1 = InstallAppsAdapter(this, refinedAppList);
        adapter2 = SelectedAppsAdapter(this, selectedAppList)



        appListRecyclerView2 = findViewById(R.id.selectedAppsRecyclerView2)
        appListRecyclerView2.layoutManager = GridLayoutManager(this, 4)
        appListRecyclerView2.adapter = adapter2





        // Create ItemTouchHelpers and attach to RecyclerViews

        val myItemTouchHelperCallback = MyItemTouchHelperCallback(this, adapter1, adapter2, appListRecyclerView2)
        val myItemTouchHelperCallback2 = MyItemTouchHelperCallback2(
            this,
            adapter1,
            adapter2,
            appListRecyclerView2
        )
        val itemTouchHelper = ItemTouchHelper(myItemTouchHelperCallback)
        val itemTouchHelper2 = ItemTouchHelper(myItemTouchHelperCallback2)
        itemTouchHelper.attachToRecyclerView(appListRecyclerView)
        itemTouchHelper2.attachToRecyclerView(appListRecyclerView2)




        //first check if local data exist?
        val sharedPreferences = applicationContext.getSharedPreferences("AppsPrefs", Context.MODE_PRIVATE)

        val appsOrder = sharedPreferences.getString("appsListOrder", null)

        if(appsOrder == null){
            loadInstalledApps()
        }else{
            loadLocalApps()
        }

        loadLocalSelectedApps();
        Toast.makeText(this, ""+selectedAppList.size, Toast.LENGTH_LONG).show()

        setToolBarTitle("SkodaCar")
    }




    private fun loadInstalledApps(){
        val mAppList: MutableList<ApplicationInfo>
        val mPackageManager: PackageManager

        mPackageManager = applicationContext.packageManager
        val allAppsList = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        mAppList = allAppsList.filter { appInfo ->
                // Filter out system apps
        (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM
        }.toMutableList()

        for (app in mAppList) {
            val appName = mPackageManager.getApplicationLabel(app).toString()
            val app = App(appName);
            appsList.add(app);

        }


        //persist data
        val appsListString = Gson().toJson(appsList)

        val sharedPreferences = applicationContext.getSharedPreferences("AppsPrefs", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()

        editor.putString("appsListOrder", appsListString)

        // Apply the changes
        editor.apply()

        adapter1.updateList(appsList)
        appListRecyclerView.adapter = adapter1

        Toast.makeText(applicationContext, "Apps from system", Toast.LENGTH_LONG).show()
    }

    private fun loadLocalApps(){

        val sharedPreferences = applicationContext.getSharedPreferences("AppsPrefs", Context.MODE_PRIVATE)

        val appsOrder = sharedPreferences.getString("appsListOrder", null)


        if (appsOrder != null) {

            refinedAppList = Gson().fromJson(appsOrder, object : TypeToken<List<App>>() {}.type)

            adapter1.updateList(refinedAppList)
            appListRecyclerView.adapter = adapter1
            Toast.makeText(applicationContext, "Apps from prefs", Toast.LENGTH_LONG).show()


        }

    }

    private fun loadLocalSelectedApps(){

        val sharedPreferences = applicationContext.getSharedPreferences("SelectedAppsPrefs", Context.MODE_PRIVATE)

        val selectedAppsOrder = sharedPreferences.getString("selectedAppsOrder", null)


        if (selectedAppsOrder != null) {

            selectedAppList = Gson().fromJson(selectedAppsOrder, object : TypeToken<List<SelectedApp>>() {}.type)

            adapter2.updateList(selectedAppList)
            appListRecyclerView2.adapter = adapter2

        }

    }



    fun CharSequence.withColorSpan(color: Int, startIndex: Int, endIndex: Int): SpannableStringBuilder {
        val spannable = SpannableStringBuilder(this)
        spannable.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    fun setToolBarTitle(title:String){
        val title = title
        val spannableTitle = title.withColorSpan(resources.getColor(R.color.toolbarTextColor), 0, title.length)
        supportActionBar?.title = spannableTitle
    }



    class MyItemTouchHelperCallback(val mContext: Context, private val mAdapter: InstallAppsAdapter, private val mAdapter2: SelectedAppsAdapter, val recyc2:RecyclerView) : ItemTouchHelper.Callback() {

        private var isDraggedOut = false

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            mAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //mAdapter.onItemDismiss(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


            val itemView = viewHolder.itemView


            // Calculate the current position of the item being dragged
            val itemRight = itemView.left + dX + itemView.width

            // Calculate the right edge of the RecyclerView
            val recyclerViewRight = recyclerView.left + recyclerView.width
            // Check if the item has touched the right edge of the RecyclerView
            if (!isDraggedOut && itemRight >= recyclerViewRight) {
                // Item has touched the right edge

                Log.d("RECYC", "Dragged out")

                val fromPosition = viewHolder.adapterPosition


                val appToAdd = mAdapter.getItem(fromPosition)
                mAdapter.notifyDataSetChanged()
                Log.d("APPTOADD", ""+appToAdd.appName)
                mAdapter2.addApp(0, appToAdd)
                isDraggedOut = true

                mAdapter.removeItem(fromPosition)




            } else {
                // Item has not touched the right edge
                // Do something else here
            }






        }


        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            isDraggedOut = false

        }

        override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
            super.onSelectedChanged(viewHolder, actionState)
            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                // Bring the second recycler view to the front
               // recyc2.bringToFront()

            }

            super.onSelectedChanged(viewHolder, actionState)
        }


    }


    //second Touch helper callback
    class MyItemTouchHelperCallback2(private val mContext: MainActivity, private val mAdapter: InstallAppsAdapter, private val mAdapter2: SelectedAppsAdapter, val recyc2:RecyclerView) : ItemTouchHelper.Callback() {

        private var isDraggedOutFromLeft = false

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }

        override fun isItemViewSwipeEnabled(): Boolean {
            return true
        }

        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            val dragFlags =
                ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            return makeMovementFlags(dragFlags, 0)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            mAdapter2.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //mAdapter.onItemDismiss(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )

            //Log.d("LEF", "Dragged out")
            val itemView = viewHolder.itemView



            // Calculate the current position of the item being dragged
            val itemLeft = itemView.right + dX




            // Calculate the right edge of the RecyclerView
            val recyclerViewLeft = recyclerView.left /* recyclerView.width*/
            //viewHolder.itemView.bringToFront()
            // Check if the item has touched the right edge of the RecyclerView
            if (!isDraggedOutFromLeft && itemLeft <= recyclerViewLeft) {
                // Item has touched the left edge
                // Do something here

                //Log.d("RECYC", "Dragged out")


                    val fromPosition = viewHolder.adapterPosition


                    val appToAdd = mAdapter2.getItem(fromPosition)

                    appToAdd?.let { mAdapter.addApp(fromPosition, it) }
                    //mAdapter.notifyDataSetChanged()

                    //mAdapter2.removeItem(fromPosition)
                    //mAdapter2.notifyDataSetChanged()

                    isDraggedOutFromLeft = true


                    mAdapter2.removeItem(fromPosition)

                    Log.d("LEFT", "item touched left edge"+fromPosition)

                // Item has touched the left edge




            } else {
                // Item has not touched the right edge
                // Do something else here
            }


        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            isDraggedOutFromLeft = false

        }


    }

}