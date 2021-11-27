package com.example.safe_return_home_service

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.w3c.dom.Text


class MainListAdapter(val context: Context, val recordlist:ArrayList<record>): BaseAdapter() {

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        /*xml파일의 View와 데이터를 연결*/
        val view: View = LayoutInflater.from(context).inflate(R.layout.information,null)
        val recordPhoto = view.findViewById<ImageButton>(R.id.recordPhoto)
        val recordName = view.findViewById<TextView>(R.id.recordName)
        val recordTime = view.findViewById<TextView>(R.id.recordTime)


        val record = recordlist[p0]
        val resourceId = context.resources.getIdentifier(record.photo, "drawable",context.packageName)
        recordPhoto.setImageResource(resourceId)
        recordName.text = record.name
        recordTime.text = record.time
        //recordPhoto?.setOnClickListener(ButtonOnClickListener())

        return view
    }

    override fun getCount(): Int {
        /*ListView에 속한 item의 전체 수를 반환한다.*/
        return recordlist.size
    }

    override fun getItem(p0: Int): Any {
        /*해당 위치의 item*/
        return recordlist[p0]
    }

    override fun getItemId(p0: Int): Long {
        /*해당 위치의 item id를 반환하는 메소드*/
        return 0
    }

}

/*class ButtonOnClickListener:View.OnClickListener{
    override fun onClick(p0: View?) {
        var setimage=0
        if(!setimage){

            recordPhoto.setImageResource()
            setimage=1
        }

    }

}*/
