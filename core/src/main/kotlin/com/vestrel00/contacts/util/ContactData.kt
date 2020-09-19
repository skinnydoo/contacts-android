package com.vestrel00.contacts.util

import com.vestrel00.contacts.entities.*

// TODO create convenience functions like addPhone(configurePhone: MutablePhone.() -> Unit)

// Dev note: The functions that return a List instead of a Sequence are useful for Java consumers
// as they will not have to convert Sequences to List.

// region Contact

/**
 * Sequence of addresses from all [rawContacts] ordered by the [Address.id].
 */
fun Contact.addresses(): Sequence<Address> = rawContacts
    .asSequence()
    .flatMap { it.addresses.asSequence() }
    .sortedBy { it.id }

/**
 * List of addresses from all [rawContacts] ordered by the [Address.id].
 */
fun Contact.addressList(): List<Address> = addresses().toList()

/**
 * Sequence of emails from all [rawContacts] ordered by the [Email.id].
 */
fun Contact.emails(): Sequence<Email> = rawContacts
    .asSequence()
    .flatMap { it.emails.asSequence() }
    .sortedBy { it.id }

/**
 * List of emails from all [rawContacts] ordered by the [Email.id].
 */
fun Contact.emailList(): List<Email> = emails().toList()

/**
 * Sequence of events from all [rawContacts] ordered by the [Event.id].
 */
fun Contact.events(): Sequence<Event> = rawContacts
    .asSequence()
    .flatMap { it.events.asSequence() }
    .sortedBy { it.id }

/**
 * List of events from all [rawContacts] ordered by the [Event.id].
 */
fun Contact.eventList(): List<Event> = events().toList()

// GroupMemberships are intentionally left out because they should never be combined.
// The native Contacts app hides the group membership UI field when viewing/editing a contact
// with more than one RawContact.

/**
 * Sequence of Ims from all [rawContacts] ordered by the [Im.id].
 */
fun Contact.ims(): Sequence<Im> = rawContacts
    .asSequence()
    .flatMap { it.ims.asSequence() }
    .sortedBy { it.id }

/**
 * List of Ims from all [rawContacts] ordered by the [Im.id].
 */
fun Contact.imList(): List<Im> = ims().toList()

/**
 * Sequence of names from all [rawContacts] ordered by the [Name.id].
 */
fun Contact.names(): Sequence<Name> = rawContacts
    .asSequence()
    .map { it.name }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of names from all [rawContacts] ordered by the [Name.id].
 */
fun Contact.nameList(): List<Name> = names().toList()

/**
 * Sequence of nicknames from all [rawContacts] ordered by the [Nickname.id].
 */
fun Contact.nicknames(): Sequence<Nickname> = rawContacts
    .asSequence()
    .map { it.nickname }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of nicknames from all [rawContacts] ordered by the [Nickname.id].
 */
fun Contact.nicknameList(): List<Nickname> = nicknames().toList()

/**
 * Sequence of notes from all [rawContacts] ordered by the [Note.id].
 */
fun Contact.notes(): Sequence<Note> = rawContacts
    .asSequence()
    .map { it.note }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of notes from all [rawContacts] ordered by the [Note.id].
 */
fun Contact.noteList(): List<Note> = notes().toList()

// Options intentionally left out because a Contact and associated RawContacts have independent
// Options.

/**
 * Sequence of organizations from all [rawContacts] ordered by the [Organization.id].
 */
fun Contact.organizations(): Sequence<Organization> = rawContacts
    .asSequence()
    .map { it.organization }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of organizations from all [rawContacts] ordered by the [Organization.id].
 */
fun Contact.organizationList(): List<Organization> = organizations().toList()

/**
 * Sequence of phones from all [rawContacts] ordered by the [Phone.id].
 */
fun Contact.phones(): Sequence<Phone> = rawContacts
    .asSequence()
    .flatMap { it.phones.asSequence() }
    .sortedBy { it.id }

/**
 * List of phones from all [rawContacts] ordered by the [Phone.id].
 */
fun Contact.phoneList(): List<Phone> = phones().toList()

// Photo intentionally left out because a Contact and associated RawContacts have independent
// Photos.

/**
 * Sequence of relations from all [rawContacts] ordered by the [Relation.id].
 */
fun Contact.relations(): Sequence<Relation> = rawContacts
    .asSequence()
    .flatMap { it.relations.asSequence() }
    .sortedBy { it.id }

/**
 * List of relations from all [rawContacts] ordered by the [Relation.id].
 */
fun Contact.relationList(): List<Relation> = relations().toList()

/**
 * Sequence of SIP addresses from all [rawContacts] ordered by the [SipAddress.id].
 */
fun Contact.sipAddresses(): Sequence<SipAddress> = rawContacts
    .asSequence()
    .map { it.sipAddress }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of SIP addresses from all [rawContacts] ordered by the [SipAddress.id].
 */
fun Contact.sipAddressList(): List<SipAddress> = sipAddresses().toList()

/**
 * Sequence of websites from all [rawContacts] ordered by the [Website.id].
 */
fun Contact.websites(): Sequence<Website> = rawContacts
    .asSequence()
    .flatMap { it.websites.asSequence() }
    .sortedBy { it.id }

/**
 * List of websites from all [rawContacts] ordered by the [Website.id].
 */
fun Contact.websiteList(): List<Website> = websites().toList()

// endregion

// region MutableContact

/**
 * Sequence of addresses from all [rawContacts] ordered by the [MutableAddress.id].
 */
fun MutableContact.addresses(): Sequence<MutableAddress> = rawContacts
    .asSequence()
    .flatMap { it.addresses.asSequence() }
    .sortedBy { it.id }

/**
 * List of addresses from all [rawContacts] ordered by the [MutableAddress.id].
 */
fun MutableContact.addressList(): List<MutableAddress> = addresses().toList()

/**
 * Adds the given [address] to the list of [RawContact.addresses] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id] sorted by the [RawContact.id].
 */
fun MutableContact.addAddress(address: MutableAddress) {
    rawContacts.firstOrNull()?.addresses?.add(address)
}

/**
 * Removes all instances of the given [address] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removeAddress(address: MutableAddress, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.addresses.removeAll(address, byReference)
    }
}

/**
 * Removes all [addresses] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllAddresses() {
    for (rawContact in rawContacts) {
        rawContact.addresses.clear()
    }
}

/**
 * Sequence of emails from all [rawContacts] ordered by the [MutableEmail.id].
 */
fun MutableContact.emails(): Sequence<MutableEmail> = rawContacts
    .asSequence()
    .flatMap { it.emails.asSequence() }
    .sortedBy { it.id }

/**
 * List of emails from all [rawContacts] ordered by the [MutableEmail.id].
 */
fun MutableContact.emailList(): List<MutableEmail> = emails().toList()

/**
 * Adds the given [email] to the list of [RawContact.emails] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id].
 */
fun MutableContact.addEmail(email: MutableEmail) {
    rawContacts.firstOrNull()?.emails?.add(email)
}

/**
 * Removes all instances of the given [email] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removeEmail(email: MutableEmail, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.emails.removeAll(email, byReference)
    }
}

/**
 * Removes all [emails] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllEmails() {
    for (rawContact in rawContacts) {
        rawContact.emails.clear()
    }
}

/**
 * Sequence of events from all [rawContacts] ordered by the [MutableEvent.id].
 */
fun MutableContact.events(): Sequence<MutableEvent> = rawContacts
    .asSequence()
    .flatMap { it.events.asSequence() }
    .sortedBy { it.id }

/**
 * List of events from all [rawContacts] ordered by the [MutableEvent.id].
 */
fun MutableContact.eventList(): List<MutableEvent> = events().toList()

/**
 * Adds the given [event] to the list of [RawContact.events] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id].
 */
fun MutableContact.addEvent(event: MutableEvent) {
    rawContacts.firstOrNull()?.events?.add(event)
}

/**
 * Removes all instances of the given [event] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removeEvent(event: MutableEvent, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.events.removeAll(event, byReference)
    }
}

/**
 * Removes all [events] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllEvents() {
    for (rawContact in rawContacts) {
        rawContact.events.clear()
    }
}

// GroupMemberships are intentionally left out because they should never be combined.
// The native Contacts app hides the group membership UI field when viewing/editing a contact
// with more than one RawContact.

/**
 * Sequence of Ims from all [rawContacts] ordered by the [MutableIm.id].
 */
fun MutableContact.ims(): Sequence<MutableIm> = rawContacts
    .asSequence()
    .flatMap { it.ims.asSequence() }
    .sortedBy { it.id }

/**
 * List of IMs from all [rawContacts] ordered by the [MutableIm.id].
 */
fun MutableContact.imList(): List<MutableIm> = ims().toList()

/**
 * Adds the given [im] to the list of [RawContact.ims] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id].
 */
fun MutableContact.addIm(im: MutableIm) {
    rawContacts.firstOrNull()?.ims?.add(im)
}

/**
 * Removes all instances of the given [im] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removeIm(im: MutableIm, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.ims.removeAll(im, byReference)
    }
}

/**
 * Removes all [ims] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllIms() {
    for (rawContact in rawContacts) {
        rawContact.ims.clear()
    }
}

/**
 * Sequence of names from all [rawContacts] ordered by the [MutableName.id].
 */
fun MutableContact.names(): Sequence<MutableName> = rawContacts
    .asSequence()
    .map { it.name }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of names from all [rawContacts] ordered by the [MutableName.id].
 */
fun MutableContact.nameList(): List<MutableName> = names().toList()

/**
 * Sets the [RawContact.name] of the first [RawContact] in [MutableContact.rawContacts] sorted by
 * the [RawContact.id].
 */
fun MutableContact.setName(name: MutableName?) {
    rawContacts.firstOrNull()?.name = name
}

/**
 * Sequence of nicknames from all [rawContacts] ordered by the [MutableNickname.id].
 */
fun MutableContact.nicknames(): Sequence<MutableNickname> = rawContacts
    .asSequence()
    .map { it.nickname }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of nicknames from all [rawContacts] ordered by the [MutableNickname.id].
 */
fun MutableContact.nicknameList(): List<MutableNickname> = nicknames().toList()

/**
 * Sets the [RawContact.nickname] of the first [RawContact] in [MutableContact.rawContacts] sorted
 * by the [RawContact.id].
 */
fun MutableContact.setNickname(nickname: MutableNickname?) {
    rawContacts.firstOrNull()?.nickname = nickname
}

/**
 * Sequence of notes from all [rawContacts] ordered by the [MutableNote.id].
 */
fun MutableContact.notes(): Sequence<MutableNote> = rawContacts
    .asSequence()
    .map { it.note }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of notes from all [rawContacts] ordered by the [MutableNote.id].
 */
fun MutableContact.noteList(): List<MutableNote> = notes().toList()

/**
 * Sets the [RawContact.note] of the first [RawContact] in [MutableContact.rawContacts] sorted by
 * the [RawContact.id].
 */
fun MutableContact.setNote(note: MutableNote?) {
    rawContacts.firstOrNull()?.note = note
}

// Options intentionally left out because a Contact and associated RawContacts have independent
// Options.

/**
 * Sequence of organizations from all [rawContacts] ordered by the [MutableOrganization.id].
 */
fun MutableContact.organizations(): Sequence<MutableOrganization> = rawContacts
    .asSequence()
    .map { it.organization }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of organizations from all [rawContacts] ordered by the [MutableOrganization.id].
 */
fun MutableContact.organizationList(): List<MutableOrganization> = organizations().toList()

/**
 * Sets the [RawContact.organization] of the first [RawContact] in [MutableContact.rawContacts]
 * sorted by the [RawContact.id].
 */
fun MutableContact.setOrganization(organization: MutableOrganization?) {
    rawContacts.firstOrNull()?.organization = organization
}

/**
 * Sequence of phones from all [rawContacts] ordered by the [MutablePhone.id].
 */
fun MutableContact.phones(): Sequence<MutablePhone> = rawContacts
    .asSequence()
    .flatMap { it.phones.asSequence() }
    .sortedBy { it.id }

/**
 * List of phones from all [rawContacts] ordered by the [MutablePhone.id].
 */
fun MutableContact.phoneList(): List<MutablePhone> = phones().toList()

/**
 * Adds the given [phone] to the list of [RawContact.phones] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id].
 */
fun MutableContact.addPhone(phone: MutablePhone) {
    rawContacts.firstOrNull()?.phones?.add(phone)
}

/**
 * Removes all instances of the given [phone] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removePhone(phone: MutablePhone, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.phones.removeAll(phone, byReference)
    }
}

/**
 * Removes all [phones] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllPhones() {
    for (rawContact in rawContacts) {
        rawContact.phones.clear()
    }
}

// Photo intentionally left out because a Contact and associated RawContacts have independent
// Photos.

/**
 * Sequence of relations from all [rawContacts] ordered by the [MutableRelation.id].
 */
fun MutableContact.relations(): Sequence<MutableRelation> = rawContacts
    .asSequence()
    .flatMap { it.relations.asSequence() }
    .sortedBy { it.id }

/**
 * List of relations from all [rawContacts] ordered by the [MutableRelation.id].
 */
fun MutableContact.relationList(): List<MutableRelation> = relations().toList()

/**
 * Adds the given [relation] to the list of [RawContact.relations] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id].
 */
fun MutableContact.addRelation(relation: MutableRelation) {
    rawContacts.firstOrNull()?.relations?.add(relation)
}


/**
 * Removes all instances of the given [relation] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removeRelation(relation: MutableRelation, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.relations.removeAll(relation, byReference)
    }
}

/**
 * Removes all [relations] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllRelations() {
    for (rawContact in rawContacts) {
        rawContact.relations.clear()
    }
}

/**
 * Sequence of SIP addresses from all [rawContacts] ordered by the [MutableSipAddress.id].
 */
fun MutableContact.sipAddresses(): Sequence<MutableSipAddress> = rawContacts
    .asSequence()
    .map { it.sipAddress }
    .filterNotNull()
    .sortedBy { it.id }

/**
 * List of SIP addresses from all [rawContacts] ordered by the [MutableSipAddress.id].
 */
fun MutableContact.sipAddressList(): List<MutableSipAddress> = sipAddresses().toList()

/**
 * Sets the [RawContact.sipAddress] of the first [RawContact] in [MutableContact.rawContacts] sorted
 * by the [RawContact.id].
 */
fun MutableContact.setSipAddress(sipAddress: MutableSipAddress?) {
    rawContacts.firstOrNull()?.sipAddress = sipAddress
}

/**
 * Sequence of websites from all [rawContacts] ordered by the [MutableWebsite.id].
 */
fun MutableContact.websites(): Sequence<MutableWebsite> = rawContacts
    .asSequence()
    .flatMap { it.websites.asSequence() }
    .sortedBy { it.id }

/**
 * List of websites from all [rawContacts] ordered by the [MutableWebsite.id].
 */
fun MutableContact.websiteList(): List<MutableWebsite> = websites().toList()

/**
 * Adds the given [website] to the list of [RawContact.websites] of the first [RawContact] in
 * [MutableContact.rawContacts] sorted by the [RawContact.id].
 */
fun MutableContact.addWebsite(website: MutableWebsite) {
    rawContacts.firstOrNull()?.websites?.add(website)
}

/**
 * Removes all instances of the given [website] from all of the [MutableContact.rawContacts].
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun MutableContact.removeWebsite(website: MutableWebsite, byReference: Boolean = false) {
    for (rawContact in rawContacts) {
        rawContact.websites.removeAll(website, byReference)
    }
}

/**
 * Removes all [websites] from all of the [MutableContact.rawContacts].
 */
fun MutableContact.removeAllWebsites() {
    for (rawContact in rawContacts) {
        rawContact.websites.clear()
    }
}

// endregion

/**
 * Removes all instances of the given [instance] from [this] collectiomn.
 *
 * By default, all **structurally equal (same content but maybe different objects)** instances will
 * be removed. Set [byReference] to true to remove all instances that are **equal by reference
 * (same object)**.
 */
@JvmOverloads
fun <T : Entity> MutableCollection<T>.removeAll(instance: T, byReference: Boolean = false) {
    if (byReference) {
        removeAll { it === instance }
    } else {
        removeAll { it == instance }
    }
}