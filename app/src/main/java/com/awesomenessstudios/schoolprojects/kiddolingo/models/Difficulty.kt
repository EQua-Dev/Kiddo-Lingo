package kiddiesgame.kotlin.models

sealed class Difficulty(
    val title: String,
    val shortDescription: String,
    val longDescription: String,
    val time: Long,
    val instruction: String,
    val color: String
) {
    class Easy(
        //val title: String = "Easy",
        //val shortDescription: String = "Simple words for beginners.",
        //val longDescription: String = "These words are commonly encountered in early childhood education and are typically easy to spell. Perfect for young children who are just starting to learn spelling.",
        //val time: Long = 0, //
        //val instruction: String = "Listen to the word pronounced, then write the word on the screen using your finger or stylus. Tap submit when you're done."
    ) : Difficulty(
        title = "Easy",
        shortDescription = "Simple words for beginners.",
        longDescription = "These words are commonly encountered in early childhood education and are typically easy to spell. Perfect for young children who are just starting to learn spelling.",
        time = 0, //
        instruction = "Listen to the word pronounced, then write the word on the screen using your finger or stylus. \nTap submit when you're done.",
        color = "green"
    )

    class Medium(
        //val title: String = "Medium",
        //val shortDescription: String = "Words with moderate difficulty.",
        //val longDescription: String = "These words are a step up from easy words and may include longer words or words with slightly more complex spellings. Suitable for children who have a basic grasp of spelling.",
        //val time: Long = 0,
        //val instruction: String = "Listen to the word pronounced, then write the word on the screen using your finger or stylus. Tap submit when you're done. You have 2 minutes to complete each word."
    ) : Difficulty(
        title = "Medium",
        shortDescription = "Words with moderate difficulty.",
        longDescription = "These words are a step up from easy words and may include longer words or words with slightly more complex spellings. Suitable for children who have a basic grasp of spelling.",
        time = 0,
        instruction = "Listen to the word pronounced, then write the word on the screen using your finger or stylus. \nTap submit when you're done. You have 2 minutes to complete each word.",
        color = "orange"
    )

    class Hard(
        //val title: String = "Hard",
        //val shortDescription: String = "Challenging words for advanced learners.",
        //val longDescription: String = "These words are more complex and may include longer words, words with irregular spellings, or less commonly used vocabulary. Ideal for children who are confident in their spelling abilities and want a challenge.",
        //val time: Long = 0,
        //val instruction: String = "Listen to the word pronounced, then write the word on the screen using your finger or stylus. Tap submit when you're done. You have 3 minutes to complete each word."
    ) : Difficulty(
        title = "Hard",
        shortDescription = "Challenging words for advanced learners.",
        longDescription = "These words are more complex and may include longer words, words with irregular spellings, or less commonly used vocabulary. Ideal for children who are confident in their spelling abilities and want a challenge.",
        time = 0,
        instruction = "Listen to the word pronounced, then write the word on the screen using your finger or stylus. \nTap submit when you're done. You have 3 minutes to complete each word.",
        color = "red"
    )

    companion object{
        val EasyInstance = Easy()
        val MediumInstance = Medium()
        val HardInstance = Hard()
    }
}
