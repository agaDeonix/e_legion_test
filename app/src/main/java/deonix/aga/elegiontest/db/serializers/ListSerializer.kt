package deonix.aga.elegiontest.db.serializers

import com.activeandroid.serializer.TypeSerializer
import com.google.gson.Gson

/**
 * Created by deonix on 11.07.16.
 */
class ListSerializer : TypeSerializer() {
    override fun getDeserializedType(): Class<*> {
        return List::class.java
    }

    override fun getSerializedType(): Class<*> {
        return String::class.java
    }

    override fun serialize(o: Any?): Any? {
        if (null == o) return null
        val json = gson.toJson(o)
        return json
    }

    override fun deserialize(o: Any?): Any? {
        if (null == o) return null
        val myClassItems = gson.fromJson(o.toString(), List::class.java!!)
        return myClassItems
    }

    companion object {
        private val gson = Gson()
    }
}