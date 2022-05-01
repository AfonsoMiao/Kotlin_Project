@XmlName("student")
data class Student(
    @XmlIgnore
    val name: String,
    @XmlTagContent
    val age: Int,
    @XmlTagContent
    val gender: Gender? = null,
    @XmlTagContent
    val type: StudentType? = null
)

enum class StudentType {
    Bachelor, Master, Doctoral
}
enum class Gender {
    Male, Female
}