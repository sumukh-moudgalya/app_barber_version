package com.defines.bloomerbarber

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import kotlinx.android.synthetic.main.service_review_list_layout.view.*
import kotlinx.android.synthetic.main.sorting_review_list.view.*
import java.util.HashMap


private var selectedSotingReview = "All"
private const val TAG = "ServiceReviewDetailActivity"

class ServiceReviewDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_service_review_detail)
        val serviceName: TextView = findViewById(R.id.activity_service_review_details_service_name)
        val backButton: ImageView = findViewById(R.id.activity_service_review_detail_back_button)
        val serviceObject =
            intent.extras!!.getSerializable(MainActivity.SERVICE_KEY) as ServiceOffered
        val ratingSegment: RatingReviews = findViewById(R.id.rating_reviews_service_detail)
        val ratingHash: HashMap<Int, Int> = HashMap<Int, Int>()
        val reviewRecycler: RecyclerView = findViewById(R.id.activity_service_review_detail_view)
        val sortingRecycler: RecyclerView =
            findViewById(R.id.activity_service_review_detail_sorting_adder)
        val numberOfReviewsText: TextView =
            findViewById(R.id.activity_service_review_detail_number_of_reviews)
        val avgRatingBar: RatingBar = findViewById(R.id.ratingBar2)
        val avgRatingText: TextView = findViewById(R.id.activity_service_review_detail_avg_rating)






        serviceName.text=serviceObject.name.uppercase()


        backButton.setOnClickListener{
            onBackPressed()
            finish()
        }




        ratingHash[1] = 0
        ratingHash[2] = 0
        ratingHash[3] = 0
        ratingHash[4] = 0
        ratingHash[5] = 0

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ratingRef = FirebaseDatabase.getInstance()
            .getReference("user_service_review/$uid/${serviceObject.timeStamp}")

        ratingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val ratingObject = it.getValue(ServiceReview::class.java)
                    var rating = Math.ceil(ratingObject!!.rating).toInt()
                    if (rating == 0) {
                        rating = 1
                    }
                    if (rating !in ratingHash) {
                        ratingHash[rating] = 1

                    } else {
                        ratingHash[rating] = ratingHash[rating]!! + 1

                    }


                }
                val colors = intArrayOf(
                    Color.parseColor("#0e9d58"),
                    Color.parseColor("#bfd047"),
                    Color.parseColor("#ffc105"),
                    Color.parseColor("#ef7e14"),
                    Color.parseColor("#d36259")
                )

                val raters = intArrayOf(
                    ratingHash[5]!!,
                    ratingHash[4]!!,
                    ratingHash[3]!!,
                    ratingHash[2]!!,
                    ratingHash[1]!!
                )

                val maxRating = ratingHash.values.maxOrNull()
                val numberOfReviews = ratingHash.values.sum()
                var totalValueOfReview = 0
                for (x in ratingHash.keys) {
                    totalValueOfReview += (x.toInt() * ratingHash[x]!!)
                }
                val avgReview: Double = totalValueOfReview.toDouble() / numberOfReviews
                ratingSegment.createRatingBars(maxRating!!, BarLabels.STYPE3, colors, raters)
                avgRatingText.text = avgReview.toString()
                avgRatingBar.rating = avgReview.toFloat()
                numberOfReviewsText.text = numberOfReviews.toString()


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        sortingAdder(sortingRecycler, this, reviewRecycler, serviceObject)
        reviewAdder(reviewRecycler, this, serviceObject, this)
    }

    private fun reviewAdder(
        reviewRecycler: RecyclerView,
        activity: Context,
        serviceObject: ServiceOffered,
        serviceReviewDetailActivity: ServiceReviewDetailActivity
    ) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        var ref = FirebaseDatabase.getInstance()
            .getReference("user_service_review/$uid/${serviceObject.timeStamp}")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {

                    val serviceReview = it.getValue(ServiceReview::class.java)
                    Log.d(TAG, "Service Review=${serviceReview!!.review.isNotEmpty()}")
                    val reviewText = serviceReview!!.review
                    if (reviewText.isNotEmpty()) {
                        if (selectedSotingReview == "All") {
                            adapter.add(
                                ReviewServiceAdder(
                                    serviceReview,
                                    serviceReviewDetailActivity
                                )
                            )
                        } else if (selectedSotingReview == serviceReview.rating.toInt()
                                .toString()
                        ) {
                            adapter.add(
                                ReviewServiceAdder(
                                    serviceReview,
                                    serviceReviewDetailActivity
                                )
                            )
                        }

                    }


                }
                reviewRecycler.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }


    private fun sortingAdder(
        sortingRecycler: RecyclerView,
        context: Context,
        reviewRecycler: RecyclerView,
        serviceObject: ServiceOffered
    ) {
        val arrayRating = arrayOf("All", "1", "2", "3", "4", "5")
        val adapter = GroupAdapter<GroupieViewHolder>()
        for (x in arrayRating) {
            adapter.add(SortingServiceReviewAdder(x, context))
        }
        sortingRecycler.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            val i = adapter.getAdapterPosition(item)
            val selectedSort = view.sorting_review_list.text.toString()
            selectedSotingReview = selectedSort
            sortingAdder(sortingRecycler, context, reviewRecycler, serviceObject)

            sortingRecycler.scrollToPosition(i - 1)
            reviewAdder(reviewRecycler, this, serviceObject, this)

        }
    }

    class ReviewServiceAdder(
        val serviceReview: ServiceReview,
        val activity: ServiceReviewDetailActivity
    ) : Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {

            val customerRef =
                FirebaseDatabase.getInstance().getReference("users/${serviceReview.userId}")

            customerRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(TAG, "======${serviceReview.review}")
                    val customer = snapshot.getValue(Customer::class.java)
                    val profilePic = viewHolder.itemView.service_review_list_profile_pic
                    Glide.with(activity).load(customer!!.profileImageUrl.toString())
                        .circleCrop().into(profilePic)
                    viewHolder.itemView.service_review_list_rating.text =
                        serviceReview.rating.toString()
                    viewHolder.itemView.service_review_list_review_text.text = serviceReview.review
                    viewHolder.itemView.service_review_list_username.text = customer.username
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })

        }

        override fun getLayout(): Int {
            return R.layout.service_review_list_layout
        }

    }


    class SortingServiceReviewAdder(val x: String, val context: Context) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val ratingValue: TextView = viewHolder.itemView.sorting_review_list
            if (x == selectedSotingReview) {
                viewHolder.itemView.soring_review_list_background.background =
                    ContextCompat.getDrawable(context, R.drawable.sorting_selected_background)
                ratingValue.setTextColor(Color.WHITE)
                ratingValue.text = x

            } else {
                ratingValue.text = x
            }

            if (x == "All") {
                viewHolder.itemView.sorint_review_list_rating.visibility = View.GONE

            }
        }

        override fun getLayout(): Int {
            return R.layout.sorting_review_list
        }

    }
}