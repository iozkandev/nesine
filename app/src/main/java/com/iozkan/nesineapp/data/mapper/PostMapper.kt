package com.iozkan.nesineapp.data.mapper

import com.iozkan.nesineapp.data.remote.dto.PostDto
import com.iozkan.nesineapp.domain.model.Post

/** Maps the network DTO to the clean domain model. */
fun PostDto.toDomain(): Post = Post(
    id = id,
    userId = userId,
    title = title,
    body = body
)

fun List<PostDto>.toDomain(): List<Post> = map { it.toDomain() }
