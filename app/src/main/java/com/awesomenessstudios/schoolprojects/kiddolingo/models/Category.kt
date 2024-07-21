package com.awesomenessstudios.schoolprojects.kiddolingo.models

sealed class Category(
    val id: Int,
    val name: String,
    val description: String,
    val ageGroup: String
) {


    class FourToSix(
        //val name: String = "Tiny Typer Tribe",
        //val id: Int = 5, //mean of 4 and 6
        //val description: String = "Welcome to the tribe where our youngest members learn the magic of spelling!",
        //val ageGroup: String = "Ages 4-6"
    ) :
        Category(
            id = 5, //mean of 4 and 6
            name = "Tiny Typer Tribe",
            description = "Welcome to the tribe where our youngest members learn the magic of spelling!",
            ageGroup = "Ages 4-6"
        )

     class SevenToNine(
        //val name: String = "Junior Jotter Jamboree",
        //val id: Int = 8, //mean of 7 and 9
        //val description: String = "Join the jamboree and embark on spelling adventures with our junior jotting enthusiasts!",
        //val ageGroup: String = "Ages 7-9"
    ) :
        Category(id = 8, //mean of 7 and 9
            name = "Junior Jotter Jamboree",
            description = "Join the jamboree and embark on spelling adventures with our junior jotting enthusiasts!",
            ageGroup = "Ages 7-9")

    class TenToTwelve(
        //val name: String = "Word Wizard Workshop",
        //val id: Int = 11, //mean of 10 and 12
        //val description: String = "Step into the workshop and unleash your inner word wizard as you conquer challenging spellings!",
        //val ageGroup: String = "Ages 10-12"
    ) :
        Category(id = 11, //mean of 10 and 12
            name = "Word Wizard Workshop",
            description = "Step into the workshop and unleash your inner word wizard as you conquer challenging spellings!",
            ageGroup = "Ages 10-12")

    class ThirteenToFifteen(
        //val name: String = "Lexical Legends League",
        //val id: Int = 14, //mean of 13 and 15
        //val description: String = "Become a legend in the league where lexical prowess reigns supreme and spelling mastery is the ultimate goal!",
        //val ageGroup: String = "Ages 13-15"
    ) : Category( id = 14, //mean of 13 and 15
        name = "Lexical Legends League",
        description = "Become a legend in the league where lexical prowess reigns supreme and spelling mastery is the ultimate goal!",
        ageGroup = "Ages 13-15")

    class SixteenToEighteen(
        //val name: String = "Verbal Vanguard Vanguard",
        //val id: Int = 17, //mean of 16 and 18
        //val description: String = "Enlist in the vanguard of verbal excellence and prove your prowess as a spelling virtuoso!",
        //val ageGroup: String = "Ages 16-18"
    ) : Category(id = 17, //mean of 13 and 15
        name = "Verbal Vanguard Vanguard",
        description = "Enlist in the vanguard of verbal excellence and prove your prowess as a spelling virtuoso!",
        ageGroup = "Ages 16-18")

    companion object {
        val FourToSixInstance = FourToSix()
        val SevenToNineInstance = SevenToNine()
        val TenToTwelveInstance = TenToTwelve()
        val ThirteenToFifteenInstance = ThirteenToFifteen()
        val SixteenToEighteenInstance = SixteenToEighteen()
    }
}
