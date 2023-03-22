import Family.Companion.memberTo

typealias Action = (String?) -> Unit

fun String.hasSpecialCharacter(): Boolean = this.contains("á")
operator fun Profession.Engineer.get(specialty: String): String {
    return "$title en $variant especializado en $specialty"
}

fun main(args: Array<String>) {
    println("Hello World!")
    val wife = Person(
        name = "Nombre desconocido",
        age = 24,
        gender = Gender.FEMALE,
        action = { it?.let { ac -> println(ac) } },
        profession = Profession.Designer("Arquitectura"),
    )

    val city = City(action = "Programando", partner = wife)
    val myGender = city.me.gender.getDescriptionAndId()
    val profession = city.profession

    println("Yo soy ${city.me.name}")
    println("Mi género es $myGender")
    println("Mi profesion es: ${ profession() }")
    println("Mi especialidad es: ${ profession["desarrollo móvil"] }")
    println("Mis habilidades son: ${ profession + "Marketing digital" }")

    city addFamilyMember wife.copy(
        name = "Ana Hernández",
        age = 55,
        profession = Profession.Business("Compras")
    )

    city.me.executeOtherAction {
        println("otra accion con la profesion: ${it ?: "Nada"}")
    }

    println("Nombres especiales")
    city.getSpecialNames().forEach {
        println(it)
    }
    println("Fin de nombres especiales")
}


object Society {
    val cities = mutableListOf<City>()
}


class City {
    private lateinit var partner: Person
    var action: String? = null
        private set
    private var family: Family
    val profession by lazy { Profession.Engineer("Software") }
    val me: Person by lazy {
        Person(
            name = "Mario Tepe",
            age = 23,
            gender = Gender.MALE,
            profession = profession,
        ) {
            println(it ?: "Not doing nothing")
        }
    }

    init {
        family = if (this::partner.isInitialized)
            me memberTo partner
        else Family(listOf(me))

        println("Sociedad creada")
    }

    constructor(action: String) {
        this.action = action
    }

    constructor(wife: Person) {
        this.partner = wife
    }

    constructor(partner: Person, action: String) {
        this.partner = partner
        this.action = action
    }

    infix fun addFamilyMember(member: Person) {
        family.members.add(member)
    }

    fun getSpecialNames(): List<String> {
        return family.members.filter { it.name.hasSpecialCharacter() }.map { it.name }
    }
}

data class Person(
    val name: String,
    val age: Int,
    val gender: Gender,
    val profession: Profession? = null,
    val action: Action
) {

    inline fun executeOtherAction(actionTwo: Action) {
        actionTwo(profession?.title)
    }
}

enum class Gender(val id: Int, val description: String) {
    MALE(1, "Hombre"),
    FEMALE(2, "Mujer"),
    OTHER(3, "Otro");

    fun getDescriptionAndId(): String = "$id - $description"

    companion object {
        @JvmStatic
        fun findGenderById(id: Int): Gender? {
            return Gender.values().firstOrNull { it.id == id }
        }
    }
}

sealed class Profession(val title: String) {

    class Engineer(val variant: String) : Profession("Ingeniero") {
        operator fun invoke() = "$title en $variant"
        operator fun plus(second: String) = "$title en $variant y $second"

    }

    class Designer(var variant: String) : Profession("Diseñador") {
        var tempLabel: String? = ""

        fun getVariantDesignerData() = variant.apply {
            tempLabel = this
        }
    }

    class Business(var variant: String) : Profession("Negocios") {
        private var counter: Int = 0

        fun getFullBusinessTitle() = "$title $variant".also { counter++ }
    }

}

class Family(people: List<Person>) {
    val members: MutableList<Person> = people.toMutableList()

    companion object {
        infix fun Person.memberTo(other: Person): Family {
            return Family(listOf(this, other))
        }
    }
}