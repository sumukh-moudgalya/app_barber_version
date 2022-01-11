package com.defines.bloomerbarber

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.taufiqrahman.reviewratings.BarLabels
import com.taufiqrahman.reviewratings.RatingReviews
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.service_review_layout.view.*
import java.io.Serializable

private val TAG = "ServiceReviewFragment"

class ServiceReviewFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myfrag = inflater.inflate(R.layout.fragment_service_review, container, false)
        val serviceRecycler: RecyclerView =
            myfrag.findViewById(R.id.fragment_service_review_service_recycler_view)

        fetchServiewReviewData(serviceRecycler,activity)

        return myfrag
    }

    private fun fetchServiewReviewData(serviceRecycler: RecyclerView, activity: FragmentActivity?) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val adapter = GroupAdapter<GroupieViewHolder>()


        val refGetServiceId = FirebaseDatabase.getInstance().getReference("services/$uid")
        refGetServiceId.addListenerForSingleValueEvent(object : ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val serviceObject = it.getValue(ServiceOffered::class.java)
                    val path = serviceObject!!.timeStamp
                    var avgReview: Double = 0.0
                    val ratingHashMap = HashMap<String, Int>()
                    ratingHashMap.put("1", 0)
                    ratingHashMap.put("2", 0)
                    ratingHashMap.put("3", 0)
                    ratingHashMap.put("4", 0)
                    ratingHashMap.put("5", 0)
                    Log.d(TAG," service ID=$path")
                    val ref =
                        FirebaseDatabase.getInstance().getReference("user_service_review/$uid/$path")
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            snapshot.children.forEach {
                                val serviceReview = it.getValue(ServiceReview::class.java)
                                Log.d(TAG,"review==${serviceReview!!.rating}")
                                avgReview += serviceReview.rating
                                if(serviceReview.rating.toInt().toString() !in ratingHashMap){
                                    ratingHashMap[serviceReview.rating.toInt().toString()]=1
                                }else{
                                    ratingHashMap[serviceReview.rating.toInt().toString()]=1+ratingHashMap[serviceReview.rating.toInt().toString()]!!
                                }
                            }
                            Log.d(TAG,"avg==${ratingHashMap}")
                            adapter.add(ServiceReviewRatingAdder(serviceObject,avgReview,ratingHashMap,activity))




                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })





                }


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
        serviceRecycler.adapter=adapter


    }

    class ServiceReviewRatingAdder(
        val serviceObject: ServiceOffered,
        val avgReview: Double,
        val ratingHash: HashMap<String, Int>,
        val activity: FragmentActivity?
    ) : Item<GroupieViewHolder>(){
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            var numberOfReviews=0.0
            var valueOfReviews=0
            for ( x in ratingHash.keys){
                numberOfReviews+=ratingHash[x]!!.toDouble()
                valueOfReviews+=(x.toInt()+ratingHash[x]!!)
            }

            val ratingReview: RatingReviews =viewHolder.itemView.service_review_layout_reviews
            val colors = intArrayOf(
                Color.parseColor("#0e9d58"),
                Color.parseColor("#bfd047"),
                Color.parseColor("#ffc105"),
                Color.parseColor("#ef7e14"),
                Color.parseColor("#d36259")
            )

            val raters = intArrayOf(
                ratingHash["5"]!!,
                ratingHash["4"]!!,
                ratingHash["3"]!!,
                ratingHash["2"]!!,
                ratingHash["1"]!!
            )

            val maxRating=ratingHash.values.maxOrNull()
            ratingReview.createRatingBars(maxRating!!, BarLabels.STYPE3, colors, raters)
            viewHolder.itemView.service_review_layout_service_name.text=serviceObject.name
            viewHolder.itemView.service_review_layout_see_more.setOnClickListener {
                val intent= Intent(activity,ServiceReviewDetailActivity::class.java)
                intent.putExtra(MainActivity.SERVICE_KEY, serviceObject as Serializable)
                activity!!.startActivity(intent)
            }


        }

        override fun getLayout(): Int {
            return R.layout.service_review_layout
        }

    }
}
