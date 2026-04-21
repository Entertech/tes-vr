package com.entertech.tes.vr.mode.mbct

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.entertech.tes.vr.R

class MbctCourseAdapter(
    private val courses: List<MbctCourse>,
    private val onCourseSelected: (MbctCourse) -> Unit
) : RecyclerView.Adapter<MbctCourseAdapter.MbctCourseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MbctCourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mbct_course, parent, false)
        return MbctCourseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MbctCourseViewHolder, position: Int) {
        holder.bind(courses[position], onCourseSelected)
    }

    override fun getItemCount(): Int = courses.size

    class MbctCourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCourseTitle: TextView = itemView.findViewById(R.id.tvCourseTitle)
        private val tvCourseSubtitle: TextView = itemView.findViewById(R.id.tvCourseSubtitle)
        private val tvCourseDuration: TextView = itemView.findViewById(R.id.tvCourseDuration)
        private val btnSelectCourse: Button = itemView.findViewById(R.id.btnSelectCourse)

        fun bind(course: MbctCourse, onCourseSelected: (MbctCourse) -> Unit) {
            tvCourseTitle.text = course.title
            tvCourseSubtitle.text = course.subtitle
            tvCourseDuration.text = "${course.stimulationMinutes}min"
            btnSelectCourse.setOnClickListener {
                onCourseSelected(course)
            }
            itemView.setOnClickListener {
                onCourseSelected(course)
            }
        }
    }
}
