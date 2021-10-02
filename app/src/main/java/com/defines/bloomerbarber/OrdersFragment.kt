package com.defines.bloomerbarber
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class OrdersFragment: Fragment() {
    // TODO: Rename and change types of parameters




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_orders, container, false)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OrdersFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}