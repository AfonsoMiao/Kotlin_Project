package Data

import Annotations.XmlIgnore
import Annotations.XmlName
import Annotations.XmlTagContent
import Enumerations.Gender
import Enumerations.StudentType

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
