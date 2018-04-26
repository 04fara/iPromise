package com.ipromise.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.Button
import android.widget.Toast
import com.ipromise.R


class ProfileFragment : Fragment() {

    private var btnTEST2: Button? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.tab2_fragment, container, false)
        btnTEST2 = view.findViewById(R.id.btnTEST2)

        btnTEST2!!.setOnClickListener { Toast.makeText(activity, "TESTING BUTTON CLICK 2", Toast.LENGTH_SHORT).show() }

        setHasOptionsMenu(true)
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_profile, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }
}