package com.example.medicare.Patient.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.medicare.Patient.activities.AllDoctorsActivity
import com.example.medicare.Patient.activities.DoctorDetailsActivity
import com.example.medicare.Patient.adapters.Banner
import com.example.medicare.Patient.adapters.BannerAdapter
import com.example.medicare.Patient.adapters.Category
import com.example.medicare.Patient.adapters.Hospital
import com.example.medicare.Patient.models.DoctorModel
import com.example.medicare.R
import com.example.medicare.databinding.FragmentPatientHomeBinding
import com.example.medicare.databinding.ItemCategoryBinding
import com.example.medicare.databinding.ItemPatientDoctorBinding
import com.example.medicare.databinding.ItemHospitalBinding
import com.google.firebase.database.*

class PatientHomeFragment : Fragment() {

    private var _binding: FragmentPatientHomeBinding? = null
    private val binding get() = _binding!!

    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        if (_binding != null) {
            val nextItem = binding.bannerViewPager.currentItem + 1
            binding.bannerViewPager.setCurrentItem(nextItem, true)
        }
    }

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPatientHomeBinding.inflate(inflater, container, false)
        database = FirebaseDatabase.getInstance().reference
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBannerSlider()
        setupCategories()
        setupDoctors()
        setupHospitals()

        // Open AllDoctorsActivity when clicking "See All" in Popular Doctor section
        binding.btnSeeAllDoctors.setOnClickListener {
            startActivity(Intent(requireContext(), AllDoctorsActivity::class.java))
        }

        // Open AllDoctorsActivity when clicking the search bar
        binding.searchBar.setOnClickListener {
            startActivity(Intent(requireContext(), AllDoctorsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 3000)
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    private fun setupBannerSlider() {
        database.child("banners").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val banners = mutableListOf<Banner>()
                for (postSnapshot in snapshot.children) {
                    val banner = postSnapshot.getValue(Banner::class.java)
                    banner?.let { banners.add(it) }
                }

                if (banners.isEmpty()) {
                    // Fallback to static if empty
                    banners.add(
                        Banner(
                            "Looking for\nSpecialist Doctors?",
                            "Schedule an appointment with our top doctors.",
                            R.drawable.ic_home_banner
                        )
                    )
                    banners.add(
                        Banner(
                            "Health Checkup\nPackages",
                            "Get up to 50% off on full body checkups.",
                            R.drawable.ic_home_banner2
                        )
                    )
                    banners.add(
                        Banner(
                            "24/7 Home Care\nServices",
                            "Professional care at your doorstep.",
                            R.drawable.ic_home_banner3
                        )
                    )
                }

                if (_binding != null) {
                    val adapter = BannerAdapter(banners)
                    binding.bannerViewPager.adapter = adapter
                    binding.bannerViewPager.offscreenPageLimit = 3
                    binding.bannerViewPager.clipToPadding = false
                    binding.bannerViewPager.clipChildren = false

                    setupDots(banners.size)

                    binding.bannerViewPager.registerOnPageChangeCallback(object :
                        ViewPager2.OnPageChangeCallback() {
                        override fun onPageSelected(position: Int) {
                            super.onPageSelected(position)
                            val realPosition = position % banners.size
                            updateDots(realPosition)
                            sliderHandler.removeCallbacks(sliderRunnable)
                            sliderHandler.postDelayed(sliderRunnable, 3000)
                        }
                    })

                    val initialPosition = (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % banners.size)
                    binding.bannerViewPager.setCurrentItem(initialPosition, false)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupDots(size: Int) {
        if (_binding == null) return
        binding.dotIndicator.removeAllViews()
        val params = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            setMargins(8, 0, 8, 0)
        }

        for (i in 0 until size) {
            val dot = ImageView(requireContext())
            dot.setImageResource(R.drawable.bg_dot_inactive)
            binding.dotIndicator.addView(dot, params)
        }
    }

    private fun updateDots(position: Int) {
        if (_binding == null) return
        for (i in 0 until binding.dotIndicator.childCount) {
            val dot = binding.dotIndicator.getChildAt(i) as ImageView
            dot.setImageResource(if (i == position) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
        }
    }

    private fun setupCategories() {
        val categoryList = mutableListOf<Category>()
        val adapter = CategoryAdapter(categoryList) { category ->
            Toast.makeText(context, "Clicked: ${category.name}", Toast.LENGTH_SHORT).show()
        }
        binding.categoryRecycler.layoutManager = GridLayoutManager(context, 4)
        binding.categoryRecycler.adapter = adapter

        database.child("categories").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (postSnapshot in snapshot.children) {
                    val category = postSnapshot.getValue(Category::class.java)
                    category?.let { categoryList.add(it) }
                }

                if (categoryList.isEmpty()) {
                    categoryList.addAll(
                        arrayListOf(
                            Category("Dentistry", R.drawable.ic_tooth, R.color.cat_dentistry),
                            Category(
                                "Cardiology",
                                R.drawable.ic_cardiology,
                                R.color.cat_cardiology
                            ),
                            Category(
                                "Pulmonology",
                                R.drawable.ic_pulmonology,
                                R.color.cat_pulmonology
                            ),
                            Category("General", R.drawable.ic_general, R.color.cat_general),
                            Category("Neurology", R.drawable.ic_brain, R.color.cat_neurology),
                            Category("Gastro", R.drawable.ic_stomach, R.color.cat_gastro),
                            Category("Laboratory", R.drawable.ic_laboratory, R.color.cat_lab),
                            Category(
                                "Vaccination",
                                R.drawable.ic_vaccine__1,
                                R.color.cat_vaccination
                            )
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun setupDoctors() {
        val doctorList = mutableListOf<DoctorModel>()
        val adapter = DoctorAdapter(doctorList) { doctor ->
            val intent = Intent(requireContext(), DoctorDetailsActivity::class.java)
            intent.putExtra("doctorUid", doctor.doctorUid)
            startActivity(intent)
        }
        binding.doctorRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.doctorRecycler.adapter = adapter

        database.child("Doctors").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                doctorList.clear()
                var count = 0
                for (postSnapshot in snapshot.children) {
                    if (count >= 3) break // Show only top 3 doctors
                    val doctor = postSnapshot.getValue(DoctorModel::class.java)
                    doctor?.let { 
                        doctorList.add(it)
                        count++
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun setupHospitals() {
        val hospitalList = mutableListOf<Hospital>()
        val adapter = HospitalAdapter(hospitalList) { hospital ->
            Toast.makeText(context, "Clicked: ${hospital.name}", Toast.LENGTH_SHORT).show()
        }
        binding.hospitalRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.hospitalRecycler.adapter = adapter

        database.child("clinics").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                hospitalList.clear()
                for (postSnapshot in snapshot.children) {
                    val hospital = postSnapshot.getValue(Hospital::class.java)
                    hospital?.let { hospitalList.add(it) }
                }
                if (hospitalList.isEmpty()) {
                    hospitalList.addAll(
                        arrayListOf(
                            Hospital(
                                "Golden Cardiology Center",
                                "555 Bridge Street, Golden Gate",
                                4.9,
                                108,
                                "2.5 km/40min",
                                "Clinic",
                                R.drawable.ic_hospital1
                            ),
                            Hospital(
                                "Sunrise Health Clinic",
                                "123 Oak Street, CA",
                                5.0,
                                58,
                                "1.2 km/30min",
                                "Hospital",
                                R.drawable.ic_hospital2
                            )
                        )
                    )
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getStarString(rating: Double): String {
        val stars = Math.round(rating).toInt()
        return "⭐".repeat(stars.coerceIn(0, 5))
    }

    // --- Adapters ---

    inner class CategoryAdapter(
        private val list: List<Category>,
        private val onClick: (Category) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemCategoryBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.categoryName.text = item.name
            if (item.icon != 0) {
                holder.binding.categoryIcon.setImageResource(item.icon)
            } else if (item.iconName != null) {
                val resId = holder.itemView.context.resources.getIdentifier(
                    item.iconName,
                    "drawable",
                    holder.itemView.context.packageName
                )
                if (resId != 0) holder.binding.categoryIcon.setImageResource(resId)
            }

            if (item.color != 0) {
                holder.binding.categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        item.color
                    )
                )
            } else if (item.colorName != null) {
                val resId = holder.itemView.context.resources.getIdentifier(
                    item.colorName,
                    "color",
                    holder.itemView.context.packageName
                )
                if (resId != 0) holder.binding.categoryCard.setCardBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        resId
                    )
                )
            }

            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = list.size
    }

    inner class DoctorAdapter(
        private val list: List<DoctorModel>,
        private val onClick: (DoctorModel) -> Unit
    ) : RecyclerView.Adapter<DoctorAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemPatientDoctorBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemPatientDoctorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.tvDoctorName.text = "Dr. ${item.fullName}"
            holder.binding.tvSpecialization.text = item.specialty
            
            // Load Doctor Image using Glide
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .placeholder(R.drawable.ic_person)
                .error(R.drawable.ic_person)
                .into(holder.binding.ivDoctor)

            holder.binding.tvReviews.text = "(${item.rating})"
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = list.size
    }

    inner class HospitalAdapter(
        private val list: List<Hospital>,
        private val onClick: (Hospital) -> Unit
    ) : RecyclerView.Adapter<HospitalAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemHospitalBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding =
                ItemHospitalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = list[position]
            holder.binding.hospitalName.text = item.name
            holder.binding.hospitalAddress.text = item.address
            holder.binding.hospitalRating.text = getStarString(item.rating)
            holder.binding.hospitalReviews.text = "(${item.reviews} Reviews)"
            holder.binding.hospitalDistance.text = item.distance
            holder.binding.hospitalType.text = item.type
            if (item.image != 0) {
                holder.binding.hospitalImage.setImageResource(item.image)
            } else if (item.imageName != null) {
                val resId = holder.itemView.context.resources.getIdentifier(
                    item.imageName,
                    "drawable",
                    holder.itemView.context.packageName
                )
                if (resId != 0) holder.binding.hospitalImage.setImageResource(resId)
            }
            holder.itemView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = list.size
    }
}
