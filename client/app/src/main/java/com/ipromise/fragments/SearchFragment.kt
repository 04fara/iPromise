package com.ipromise.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.ipromise.R

class SearchFragment : Fragment() {

    private var btnTEST: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tab3_fragment, container, false)
        btnTEST = view.findViewById(R.id.btnTEST3)

        btnTEST!!.setOnClickListener { Toast.makeText(activity, "TESTING BUTTON CLICK 3", Toast.LENGTH_SHORT).show() }

        return view
    }

    companion object {
        private val TAG = "Tab3Fragment"
    }
}