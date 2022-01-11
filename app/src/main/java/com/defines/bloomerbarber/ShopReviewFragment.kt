package com.defines.bloomerbarber

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.taufiqrahman.reviewratings.BarLabels
import com.taufiqrahman.reviewratings.RatingReviews
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.sorting_review_list.view.*
import java.io.Serializable
import java.lang.Math.ceil

import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private var selectedSotingReview = "All"

/**
 * A simple [Fragment] subclass.
 * Use the [ReviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private val TAG = "ReviewFragment"

class ReviewFragment : Fragment() {
    // TODO: Rename and change types of parameters


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val myfrag = inflater.inflate(R.layout.fragment_shop_review, container, false)

        val ratingSegment: RatingReviews = myfrag.findViewById(R.id.rating_reviews)
        val ratingHash: HashMap<Int, Int> = HashMap<Int, Int>()
        val reviewRecycler: RecyclerView = myfrag.findViewById(R.id.fragment_review_recycler_view)
        val sortingRecycler: RecyclerView = myfrag.findViewById(R.id.fragment_review_sorting_adder)
        val numberOfReviewsText:TextView=myfrag.findViewById(R.id.fragment_shop_review_number_of_reviews)
        val avgRatingBar:RatingBar=myfrag.findViewById(R.id.ratingBar)
        val avgRatingText:TextView=myfrag.findViewById(R.id.fragment_shop_review_avg_rating)
        ratingHash[1] = 0
        ratingHash[2] = 0
        ratingHash[3] = 0
        ratingHash[4] = 0
        ratingHash[5] = 0

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ratingRef = FirebaseDatabase.getInstance().getReference("user_shop_reviews/$uid/")

        ratingRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    val ratingObject = it.getValue(RatingUserShop::class.java)
                    var rating = ceil(ratingObject!!.rating).toInt()
                    if (rating == 0) {
                        rating = 1
                    }
                    if (rating !in ratingHash) {
                        ratingHash[rating] = ratingObject.numberOfReviews

                    } else {
                        ratingHash[rating] = ratingObject.numberOfReviews + ratingHash[rating]!!

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

                val maxRating=ratingHash.values.maxOrNull()
                val numberOfReviews=ratingHash.values.sum()
                var totalValueOfReview=0
                for( x in ratingHash.keys){
                    totalValueOfReview+=(x.toInt()*ratingHash[x]!!)
                }
                val avgReview:Double=totalValueOfReview.toDouble()/numberOfReviews
                ratingSegment.createRatingBars(maxRating!!, BarLabels.STYPE3, colors, raters)
                avgRatingText.text=avgReview.toString()
                avgRatingBar.rating= avgReview.toFloat()
                numberOfReviewsText.text= numberOfReviews.toString()


            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
        sortingAdder(sortingRecycler, activity, myfrag.context, reviewRecycler)
        reviewAdder(reviewRecycler, myfrag.context, activity)


        return myfrag
    }

    private fun sortingAdder(
        sortingRecycler: RecyclerView,
        activity: FragmentActivity?,
        context: Context,
        reviewRecycler: RecyclerView
    ) {
        val arrayRating = arrayOf("All", "1", "2", "3", "4", "5")
        val adapter = GroupAdapter<GroupieViewHolder>()
        for (x in arrayRating) {
            Log.d(TAG, "$x valueeee")
            adapter.add(SortingAdder(x, activity, context))
        }
        sortingRecycler.adapter = adapter
        adapter.setOnItemClickListener { item, view ->
            val i = adapter.getAdapterPosition(item)
            val selectedSort = view.sorting_review_list.text.toString()
            selectedSotingReview = selectedSort
            sortingAdder(sortingRecycler, activity, context, reviewRecycler)
            reviewAdder(reviewRecycler, context, activity)
            sortingRecycler.scrollToPosition(i - 1)


        }
    }

    private fun reviewAdder(
        reviewRecycler: RecyclerView,
        activity: Context,
        activity1: FragmentActivity?
    ) {
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        var ref = FirebaseDatabase.getInstance().getReference("barber_orders/$uid/completed/")


        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hashReview: HashMap<String, BookingElement> = HashMap<String, BookingElement>()
                snapshot.children.forEach {
                    val bookingElement = it.getValue(BookingElement::class.java)
                    if (bookingElement!!.isReviewed) {
                        if (bookingElement.user_uid in hashReview) {
                            if (bookingElement.timeStamp > hashReview[bookingElement.user_uid]!!.timeStamp) {
                                hashReview[bookingElement.user_uid] = bookingElement
                            }
                        } else {
                            hashReview[bookingElement.user_uid] = bookingElement
                        }
                    }
                }

                val adapter = GroupAdapter<GroupieViewHolder>()
                for (x in hashReview.keys) {
                    Log.d(TAG, "${hashReview[x]!!.rating.toInt().toString()}")
                    if (selectedSotingReview == "All") {
                        adapter.add(ReviewAdder(hashReview[x]!!, activity, activity1))
                    } else if (selectedSotingReview == hashReview[x]!!.rating.toInt().toString()) {
                        Log.d(TAG, "${hashReview[x]!!.rating.toInt().toString()}")
                        adapter.add(ReviewAdder(hashReview[x]!!, activity, activity1))
                    }

                }

                reviewRecycler.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    class ReviewAdder(
        val bookingElement: BookingElement,
        val activity: Context,
        val activity1: FragmentActivity?
    ) :
        Item<GroupieViewHolder>() {
        override fun bind(viewHolder: GroupieViewHolder, position: Int) {
            val profilePic: ImageView =
                viewHolder.itemView.findViewById(R.id.review_list_profile_pic)
            val username: TextView = viewHolder.itemView.findViewById(R.id.review_list_username)
            val ratingText: TextView = viewHolder.itemView.findViewById(R.id.review_list_rating)
            val reviewText: TextView =
                viewHolder.itemView.findViewById(R.id.review_list_review_text)
            val bookingElementInfoButton: TextView =
                viewHolder.itemView.findViewById(R.id.review_list_see_order_info_button)
            val user = FirebaseAuth.getInstance().currentUser!!
            Glide.with(activity).load(FirebaseAuth.getInstance().currentUser!!.photoUrl.toString())
                .circleCrop().into(profilePic)
            username.text = user.displayName
            ratingText.text = bookingElement.rating.toString()
            reviewText.text = """"""" + bookingElement.reviewText + """""""

            bookingElementInfoButton.setOnClickListener {
                val intent = Intent(activity1, BookingAppointmentDetailsActivity::class.java)
                intent.putExtra(MainActivity.BOOKING_ELEMENT_KEY, bookingElement as Serializable)
                activity!!.startActivity(intent)

            }


        }

        override fun getLayout(): Int {
            return R.layout.review_list
        }

    }

    class SortingAdder(val x: String, val activity: FragmentActivity?, val context: Context) :
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

    companion object {

        @JvmStatic
        fun newInstance() =
            ReviewFragment().apply {
                arguments = Bundle().apply {}
            }
    }
}