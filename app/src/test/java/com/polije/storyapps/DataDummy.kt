package com.polije.storyapps

import com.polije.storyapps.model.Story

object DataDummy {

    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                id = "id_$i",
                name = "Name $i",
                description = "Description $i",
                photoUrl = "https://picsum.photos/600/400?random=\$",
                createdAt = "2023-01-01T00:00:00Z",
                lat = -7.797068,
                lon = 110.370529
            )
            items.add(story)
        }
        return items
    }
}