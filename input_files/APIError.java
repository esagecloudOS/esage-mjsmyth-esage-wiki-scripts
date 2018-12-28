/**
 * Copyright (C) 2008 - Abiquo Holdings S.L. All rights reserved.
 *
 * Please see /opt/abiquo/tomcat/webapps/legal/ on Abiquo server
 * or contact contact@abiquo.com for licensing information.
 */
package com.abiquo.api.exceptions;

import static com.abiquo.model.enumerator.RemoteServiceType.NODE_COLLECTOR;
import static com.abiquo.model.enumerator.RemoteServiceType.VIRTUAL_FACTORY;
import static com.abiquo.model.enumerator.RemoteServiceType.VIRTUAL_SYSTEM_MONITOR;

import java.util.Arrays;
import java.util.Comparator;

import org.apache.wink.common.http.HttpStatus;

import com.abiquo.api.services.cloud.VirtualMachineMetadataService;
import com.abiquo.event.model.enumerations.EntityAction.Action;
import com.abiquo.event.model.enumerations.EntityAction.DATACENTER;
import com.abiquo.event.model.enumerations.EntityAction.KEYS;
import com.abiquo.event.model.interfaces.AbiquoError;
import com.abiquo.hypervisor.model.builder.BuilderException.VirtualMachineDescriptionBuilderError;
import com.abiquo.hypervisor.plugin.exception.HypervisorPluginError;
import com.abiquo.model.transport.error.CommonError;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplate;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.infrastructure.management.Rasd;
import com.google.common.base.Function;

/**
 * Contains all the errors notified by the API.
 * 
 * @author eruiz
 */
public enum APIError implements AbiquoError
{
    // STATUSCODES
    STATUS_BAD_REQUEST(fromStatus(400), "Request not valid"), //
    STATUS_UNAUTHORIZED(fromStatus(401), "This request requires user authentication"), //
    STATUS_FORBIDDEN(fromStatus(403), "Access denied"), //
    STATUS_NOT_FOUND(fromStatus(404), "The requested resource does not exist"), //
    STATUS_METHOD_NOT_ALLOWED(fromStatus(405), "The resource does not expose this method"), //
    STATUS_NOT_ACCEPTABLE_VERSION(fromStatus(406), "Invalid version parameter for 'Accept' header"), //
    STATUS_CONFLICT(fromStatus(409), "Conflict"), //
    STATUS_HEADER_VERSION_MANDATORY(fromStatus(412) + "-HEADER",
        "Header 'X-abiquo-version' is mandatory"), //
    STATUS_UNPROVISIONED(fromStatus(412), "Error releasing resources on the hypervisor"), //
    STATUS_UNSUPPORTED_MEDIA_TYPE_VERSION(fromStatus(415) + "-VERSION",
        "Invalid 'Content-type' version"), //
    STATUS_UNSUPPORTED_MEDIA_TYPE(fromStatus(415),
        "The Abiquo API currently only supports application/XML Media Type"), //
    STATUS_INTERNAL_SERVER_ERROR(fromStatus(500), "Unexpected exception"), //
    SERVICE_UNAVAILABLE_ERROR(fromStatus(503), "Service unavailable: try again in a few moments"), SERVICE_UNAVAILABLE_ERROR_VOLUME(
        fromStatus(503) + "_PERSISTENT", "Service unavailable: try again in a few moments"), STATUS_CONFLICT_STALE_OBJECT(
        fromStatus(409) + "-StaleObject", "Another request has updated this entity"), //
    STATUS_NOT_IMPLEMENTED(fromStatus(409) + "-NotImpemented", "Not Implemented"), //

    // GENERIC
    MALFORMED_URI("GEN-0", "Malformed URI"), INVALID_ID("GEN-1", "Identifier cannot be 0"), CONSTRAINT_VIOLATION(
        "GEN-2", "Invalid XML document; please ensure all mandatory fields are correct"), UNMARSHAL_EXCEPTION(
        "GEN-3", "Invalid XML document"), FORBIDDEN("GEN-4",
        "Not enough permissions to perform this action"), INVALID_CREDENTIALS("GEN-5",
        "Invalid credentials"), INVALID_LINK("GEN-6", "Invalid link reference"), WHITE_NAME(
        "GEN-7", "The property 'name' must not have whitespace at the beginning or the end"), WHITE_SYMBOL(
        "GEN-8", "The property 'symbol' must not have whitespace at the beginning or the end"), WHITE_DESCRIPTION(
        "GEN-9", "The property 'description' must not have whitespace at the beginning or the end"), REQUIRED_ID(
        "GEN-10", "Identifier is required"), ID_FORBIDDEN("GEN-11",
        "The identifier field cannot be used to create entities"), INVALID_PRIVATE_NETWORK_TYPE(
        "GEN-12", "Invalid private network type"), INTERNAL_SERVER_ERROR("GEN-13",
        "Unexpected error"), GENERIC_OPERATION_ERROR("GEN-14",
        "The operation could not be performed. Please contact your System Administrator"), NOT_ENOUGH_PRIVILEGES(
        "GEN-15", "Not enough privileges to perform this operation"), INCOHERENT_IDS("GEN-16",
        "The ID parameter is different from the entity ID"),

    // PLUGIN
    PLUGIN_NOT_FOUND("GEN-17", "The plugin type was not found"), PLUGIN_UNSUPPORTED_OPERATION(
        "GEN-18", "The requested operation is unsupported by the hypervisor plugin"), INVALID_PLUGIN_HOST_CONNECTION(
        "GEN-19", "The supplied connection is not valid for the hypervisor plugin"), PLUGIN_UNSUPPORTED_DEFINITION(
        "GEN-20",
        "The requested virtual machine definition cannot be applied by the hypervisor plugin"), PLUGIN_IS_NOT_CLOUD_PROVIDER(
        "GEN-21", "The requested plugin is not a cloud provider plugin type"),

    INVALID_QUERY_PARAM("GEN-22",
        "Query params are invalid or some required query param is missing"),

    // LINK
    LINK_REL_REQUIRED("LINK-0", "The 'rel' property of the link is required"), LINK_HREF_REQUIRED(
        "LINK-1", "The 'href' property of the link is required"),

    // DATACENTER
    NON_EXISTENT_DATACENTER("DC-0", "The requested datacenter does not exist"), DATACENTER_DUPLICATED_NAME(
        "DC-3", "There is already a datacenter with that name"), DATACENTER_NOT_ALLOWED("DC-4",
        "The current enterprise cannot use this datacenter"), DATACENTER_DELETE_STORAGE("DC-5",
        "Cannot delete datacenter with storage devices associated"), DATACENTER_DELETE_VIRTUAL_DATACENTERS(
        "DC-6", "Cannot delete datacenter with virtual datacenters associated"), DATACENTER_QUEUE_NOT_CONFIGURED(
        "DC-7",
        "Datacenter queues are not configured (check the BPM and virtual factory remote services)"), MISSING_DATACENTER_LINK(
        "DC-8", "Missing datacenter link"), INVALID_DATACENTER_LINK("DC-9",
        "Invalid datacenter link"), DATACENTER_NOT_ENOUGH_RESOURCES(
        "DC-10",
        "There are not enough resources available in the datacenter. Try again in a few minutes and if the error persists, please contact your System Administrator."),

    // ENTERPRISE
    NON_EXISTENT_ENTERPRISE("EN-0", "The requested enterprise does not exist"), ENTERPRISE_DUPLICATED_NAME(
        "ENTERPRISE-4", "Duplicate name for an enterprise"), ENTERPRISE_DELETE_ERROR_WITH_VDCS(
        "ENTERPRISE-5", "Cannot delete enterprise with virtual datacenters associated"), ENTERPRISE_DELETE_OWN_ENTERPRISE(
        "ENTERPRISE-6", "Cannot delete the current user's enterprise"), ENTERPRISE_EMPTY_NAME(
        "ENTERPRISE-7", "Enterprise name cannot be empty"), MISSING_ENTERPRISE_LINK("ENTERPRISE-8",
        "Missing enterprise link"), ENTERPRISE_WITH_BLOCKED_USER(
        "ENTERPRISE-9",
        "Cannot delete enterprise because some users have roles that cannot be deleted, please change their enterprise before continuing"), ENTERPRISE_NOT_ALLOWED_DATACENTER(
        "ENTERPRISE-10", "The enterprise does not have permission to use the requested datacenter"), INVALID_ENTERPRISE_LINK(
        "ENTERPRISE-11", "Invalid enterprise identifier in the enterprise link"), MISSING_PRICING_TEMPLATE_LINK(
        "ENTERPRISE-12", "Missing link to the pricing template"), PRICING_TEMPLATE_PARAM_NOT_FOUND(
        "ENTERPRISE-13", "Missing pricing template parameter"), NOT_ENOUGH_PRIVILEGES_TO_CONFIGURE_CHEF(
        "ENTERPRISE-14", "You do not have enough privileges to configure the Chef properties"), NON_EXISTENT_ENTERPRISE_PROPS(
        "ENPROP-0", "The requested enterprise properties do not exist"), ENTERPRISE_THEME_PRIVILEGES(
        "ENTERPRISE-15", "You do not have enough privileges to set the enterprise theme"),

    // LIMITS: Common for Enterprise and virtual datacenter
    LIMITS_INVALID_HARD_LIMIT_FOR_VLANS_PER_VDC(
        "LIMIT-6",
        "Invalid VLAN hard limit; this cannot be greater than the number of VLANS per virtual datacenter: {0}"), LIMITS_DUPLICATED(
        "LIMIT-7", "Duplicate limits by enterprise and datacenter"), LIMITS_NOT_EXIST("LIMIT-8",
        "Limits by enterprise and datacenter do not exist"), //
    ENTERPRISE_LIMIT_EDIT_ARE_SURPASSED("LIMIT-9",
        "Cannot edit resource limits; current enterprise allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), //
    DATACENTER_LIMIT_EDIT_ARE_SURPASSED(
        "LIMIT-10",
        "Cannot edit resource limits; current enterprise and datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), DATACENTER_LIMIT_DELETE_VDCS(
        "LIMIT-11",
        "Cannot remove enterprise access to this datacenter because the datacenter is used by virtual datacenter(s) belonging to the enterprise"), DATACENTER_LIMIT_DELETE_NETWORK(
        "LIMIT-12",
        "Cannot remove enterprise access to this datacenter because the datacenter has network(s) belonging to the enterprise"), ENTERPRISE_LIMIT_EDIT_ARE_SURPASSED_SHORT(
        "LIMIT-13",
        "Cannot edit resource limits; current enterprise allocation exceeds the new specified limits "), DATACENTER_LIMIT_EDIT_ARE_SURPASSED_SHORT(
        "LIMIT-14",
        "Cannot edit resource limits; current datacenter allocation exceeds the new specified limits "), DATACENTER_LINK_TIER_INVALID_LINK(
        "LIMIT-15", "Invalid link of the tier to allow"), DATACENTER_LINK_TIER_INVALID_DATACENTER(
        "LIMIT-16",
        "Datacenter identifier from tier link does not match the identifier from datacenter link"), DATACENTER_LINK_TIER_CANNOT_BE_RESTRICTED(
        "LIMIT-17",
        "Cannot restrict the tier because the enterprise has already created volumes in it"), DATACENTER_LIMIT_DC_OUT_OF_SCOPE(
        "LIMIT-18", "Not enough permissions to create the limit in this datacenter"), DATACENTER_LIMIT_ALLOW_TIER_DC_OUT_OF_SCOPE(
        "LIMIT-19", "Not enough permissions to allow new tiers in this datacenter"),
    // VIRTUAL DATACENTER
    NON_EXISTENT_VIRTUAL_DATACENTER("VDC-0", "The requested virtual datacenter does not exist"), VIRTUAL_DATACENTER_INVALID_HYPERVISOR_TYPE(
        "VDC-1", "Invalid hypervisor type for this virtual datacenter"), VIRTUAL_DATACENTER_CONTAINS_VIRTUAL_APPLIANCES(
        "VDC-2",
        "This virtual datacenter contains virtual appliances and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_CONTAINS_RESOURCES(
        "VDC-3",
        "This virtual datacenter has volumes attached and cannot be deleted without removing them first"), VIRTUAL_DATACENTER_INVALID_NETWORKS(
        "VDC-4", "This virtual datacenter has networks without IP addresses"), VIRTUAL_DATACENTER_LIMIT_EDIT_ARE_SURPRASED(
        "VDC-5",
        "Cannot edit resource limits; current virtual datacenter allocation exceeds the new specified limits "
            + "(see SYSTEM traces in order to determine which resources are at HARD limit)"), VIRTUAL_DATACENTER_MUST_HAVE_NETWORK(
        "VDC-6", "Virtual datacenter must be created with a private network"), VIRTUAL_DATACENTER_MINIMUM_VLAN(
        "VDC-7", "Virtual datacenter must have at least one private VLAN"), VIRTUAL_DATACENTER_LIMIT_EDIT_ARE_SURPRASED_SHORT(
        "VDC-8",
        "Cannot edit resource limits; current virtual datacenter allocation exceeds the new specified limits "), INVALID_VDC_LINK(
        "VDC-9", "The provided virtual datacenter link is invalid"),

    // VLANS
    VLANS_PRIVATE_MAXIMUM_REACHED("VLAN-0",
        "You have reached the maximum VLANs that you can create in this virtual datacenter"), VLANS_DUPLICATED_VLAN_NAME_VDC(
        "VLAN-1", "Cannot create two VLANs with the same name in a virtual datacenter"), VLANS_PRIVATE_ADDRESS_WRONG(
        "VLAN-2", "Cannot use any address outside the private range"), VLANS_TOO_BIG_NETWORK(
        "VLAN-3",
        "For performance reasons, the platform does not allow you to create large networks"), VLANS_TOO_BIG_NETWORK_II(
        "VLAN-4", "This network can have a netmask of between 30 and 22. Use a value above 22"), VLANS_TOO_SMALL_NETWORK(
        "VLAN-5", "This network can have a netmask of between 30 and 22. Use a value below 30"), VLANS_INVALID_NETWORK_AND_MASK(
        "VLAN-6", "The network does not match the mask. Check your request"), VLANS_GATEWAY_OUT_OF_RANGE(
        "VLAN-7", "Gateway address out of range. It must be in the IP address range"), VLANS_NON_EXISTENT_VIRTUAL_NETWORK(
        "VLAN-8", "The requested virtual network does not exist"), VLANS_AT_LEAST_ONE_DEFAULT_NETWORK(
        "VLAN-9", "There must be at least one default VLAN in each virtual datacenter"), VLANS_EDIT_INVALID_VALUES(
        "VLAN-10",
        "Attributes 'address', 'mask' and 'tag' cannot be changed when editing a private VLAN."), VLANS_DEFAULT_NETWORK_CAN_NOT_BE_DELETED(
        "VLAN-11", "The VLAN cannot be deleted because it is the default VLAN of this enterprise"), VLANS_WITH_USED_IPS_CAN_NOT_BE_DELETED(
        "VLAN-12", "Cannot delete a VLAN with IPs used by virtual machines"), VLANS_TAG_MANDATORY_FOR_PUBLIC_VLANS(
        "VLAN-13", "Field 'tag' is mandatory when you create public VLANs"), VLANS_WITH_PURCHASED_IPS_CAN_NOT_BE_DELETED(
        "VLAN-14", "Cannot delete a VLAN with IPs purchased by enterprises"), VLANS_DUPLICATED_VLAN_NAME_DC(
        "VLAN-15", "Cannot create two VLANs with the same name in a datacenter"), VLANS_TAG_INVALID(
        "VLAN-16", "VLAN tag out of limits"), VLANS_NON_EXISTENT_PUBLIC_IP("VLAN-17",
        "The requested IP object does not exist"), VLANS_IP_EDIT_INVALID_VALUES("VLAN-18",
        "Only 'quarantine' and 'available' attributes can be modified when editing an IP"), VLANS_PUBLIC_EDIT_INVALID_VALUES(
        "VLAN-19",
        "Attributes 'address' and 'mask' cannot be changed when editing a public, external or unmanaged network"), VLANS_PUBLIC_IP_NOT_TO_BE_PURCHASED(
        "VLAN-20", "The IP does not exist or is not available"), VLANS_PUBLIC_IP_NOT_PURCHASED(
        "VLAN-21", "The IP does not exist or is not purchased"), VLANS_PUBLIC_IP_BUSY("VLAN-22",
        "This IP address is currently used by a virtual machine. It cannot be released"), VLANS_PRIVATE_IP_INVALID_LINK(
        "VLAN-23", "Invalid link to private IP address to create NIC"), VLANS_IP_LINK_INVALID_VDC(
        "VLAN-24", "Invalid virtual datacenter identifier in the IP link"), VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE(
        "VLAN-25", "The IP address is already used by another virtual machine"), VLANS_PUBLIC_IP_INVALID_LINK(
        "VLAN-26", "Invalid link to public IP address to create NIC"), VLANS_IP_CAN_NOT_BE_DEASSIGNED_DUE_CONFIGURATION(
        "VLAN-27",
        "Cannot release this IP from the virtual machine because the configured default gateway is in the same subnet. "
            + "Please choose a different gateway before removing this IP"), VLANS_NIC_NOT_FOUND(
        "VLAN-28", "The NIC does not exist"), VLANS_CAN_NOT_DETACH_LAST_NIC("VLAN-29",
        "Every virtual machine should have at least one NIC"), VLANS_REORDER_NIC_INVALID_LINK(
        "VLAN-30", "Invalid link to reorder NICs on a virtual machine"), VLANS_REORDER_NIC_INVALID_LINK_VALUES(
        "VLAN-31",
        "Invalid link values (virtual datacenter, virtual appliance and/or virtual machine identifiers) for reordering NICs on a virtual machine"), VLANS_PUBLIC_IP_EDIT_NOT_AVAILABLE_PURCHASED(
        "VLAN-32", "Cannot set the IP as 'not available' while it is purchased by an enterprise"), VLANS_PUBIC_IP_CAN_NOT_RELEASE(
        "VLAN-33", "Cannot release a public IP while it is assigned to a virtual machine"), VLANS_NON_EXISTENT_CONFIGURATION(
        "VLAN-34", "The configuration does not exist"), VLANS_CAN_NOT_ASSIGN_TO_DEFAULT_ENTERPRISE(
        "VLAN-35",
        "Cannot assign the external VLAN as default because it is not assigned to any enterprise"), VLANS_VIRTUAL_DATACENTER_SHOULD_HAVE_A_DEFAULT_VLAN(
        "VLAN-36",
        "Unable to find default VLAN in virtual datacenter. Inconsistent state in database"), VLANS_INVALID_ENTERPRISE_LINK(
        "VLAN-37", "Invalid enterprise identifier in the enterprise link"), VLANS_IP_ALREADY_ASSIGNED_TO_A_VIRTUAL_DATACENTER(
        "VLAN-38", "The IP address is already assigned to a virtual datacenter"), VLANS_WITH_IPS_ASSIGNED_TO_VDC(
        "VLAN-39", "Cannot delete a VLAN with IPs assigned to a virtual datacenter"), VLANS_EXTERNAL_VLAN_IN_ANOTHER_DATACENTER(
        "VLAN-40",
        "The requested external VLAN does not belong to the same datacenter as the virtual datacenter"), VLANS_INVALID_IP_FORMAT(
        "VLAN-41", "IP format is invalid"), VLANS_IP_DOES_NOT_EXISTS("VLAN-42",
        "The IP does not exist"), VLANS_CANNOT_DELETE_DEFAULT("VLAN-43",
        "This is the default VLAN for the virtual datacenter and it cannot be deleted"), VLANS_EXTERNAL_VLAN_OF_ANOTHER_ENTERPRISE(
        "VLAN-44", "The external VLAN belongs to another enterprise"), VLANS_IP_NOT_AVAILABLE(
        "VLAN-45", "The IP address is not available to be used by a virtual machine"), VLANS_NON_EXISTENT_EXTERNAL_IP(
        "VLAN-46", "The requested IP object does not exist"), VLANS_ASSIGNED_TO_ANOTHER_VIRTUAL_DATACENTER(
        "VLAN-47",
        "Cannot change enterprise because this network is used as the default by a virtual datacenter"), VLANS_NOT_UNMANAGED(
        "VLAN-48", "The virtual network is not unmanaged "), VLANS_UNMANAGED_WITH_VM_CAN_NOT_BE_DELETED(
        "VLAN-49", "Cannot delete unmanaged networks associated with virtual machines"), VLANS_MISSING_ENTERPRISE_LINK(
        "VLAN-50", "Enterprise link with rel 'enterprise' is mandatory"), VLANS_IP_IS_IN_QUARANTINE(
        "VLAN-51", "The IP %s is in quarantine"), VLANS_IP_REL_REPEATED_LINKS("VLAN-52",
        "Invalid input. Repeated NIC attachment value is not allowed"), VLAN_EDIT_IPS_ASSIGNED(
        "VLAN-53",
        "Cannot edit the name and gateway of the VLAN because it has IPs assigned to deployed virtual machines"), VLANS_IP_QUARANTINE_UNMANAGED_NOT_ALLOWED(
        "VLAN-54", "Unmanaged IP cannot be put in quarantine"), VLAN_NST_FROM_OTHER_DATACENTER(
        "VLAN-55", "The network service type does not belong to the same datacenter as the VLAN"), VLANS_NOT_EXTERNAL(
        "VLAN-56", "The virtual network is not an external network"), VLANS_IP_HREF_REPEATED_LINKS(
        "VLAN-57", "Invalid input. Repeated IP href value is not allowed"), NON_EXISTENT_VLAN(
        "VLAN-58", "The requested VLAN does not exist"), VLANS_IP_NOT_UNMANAGED("VLAN-59",
        "The IP supplied does not belong to an unmanaged network"), VLANS_EDIT_UNMANAGED_IP_WRONG_VALUES(
        "VLAN-60", "When editing an UnmanagedIp, you may only change the attribute 'ip'"), VLANS_INVALID_CONTENT_TYPE_NOT_EXTERNAL(
        "VLAN_61", "The requested IPs are not from an external network"), VLANS_INVALID_CONTENT_TYPE_NOT_UNMANAGED(
        "VLAN_62", "The requested IPs are not from an unmanaged network"), VLANS_INVALID_CONTENT_TYPE_NOT_PUBLIC(
        "VLAN_63", "The requested IPs are not from a public network"), VLANS_INVALID_CONTENT_TYPE_NOT_EXTERNAL_SINGLE(
        "VLAN_64", "The requested IP is not from an external network"), VLANS_INVALID_CONTENT_TYPE_NOT_UNMANAGED_SINGLE(
        "VLAN_65", "The requested IP is not from an unmanaged network"), VLANS_INVALID_CONTENT_TYPE_NOT_PUBLIC_SINGLE(
        "VLAN_66", "The requested IP is not from a public network"), VLANS_TAG_FORBIDDEN_FOR_PRIVATE_VLANS(
        "VLAN-67", "Private VLAN tags are automatically allocated and cannot be changed"), VLANS_IP_IS_NOT_AVAILABLE(
        "VLAN-68", "The IP %s is not available"), VLANS_IP4_CANNOT_CREATE("VLAN-69",
        "Cannot create an IP in networks other than IPv6 networks"), VLANS_IP_OUTSIDE_RANGE(
        "VLAN-70", "The IP does not belong to the network"), VLANS_IPS_EXHAUSTED("VLAN-71",
        "All IPs in the network are already created"), VLANS_IP_ALREADY_CREATED("VLAN-72",
        "The requested IP is already created in the network"), VLANS_INCOMPATIBLE_IP_VERSION(
        "VLAN-73", "The supplied IP format does not match the IP version"), VLANS_CANNOT_CHANGE_IP_VERSION(
        "VLAN-74", "Cannot change the IP version of a VLAN"), VLANS_INVALID_IPV6_MASK("VLAN-75",
        "IPv6 networks can have a netmask of 64, 56 or 48"), VLANS_CANNOT_GET_MAC("VLAN-76",
        "Cannot get a new MAC address for the IP"), VLANS_CANT_PRECREATE_UNMANAGED_IPS("VLAN-77",
        "Cannot create unmanaged IPs. Abiquo does not create IPs for unmanaged networks"), VLANS_IPV6_STRICT_CANT_BE_MANUAL(
        "VLAN-78", "Cannot create IPs manually in strict networks"), VLANS_EDIT_INVALID_VALUES_IPV6(
        "VLAN-79", "Attributes 'gateway' and 'strict' cannot be changed when editing an IPv6 VLAN"), VLANS_IPV4_CANNOT_BE_STRICT(
        "VLAN-80", "IPv4 VLANs cannot be strict"), VLANS_ADDRESS_REQUIRED("VLAN-81",
        "Address attribute is required"), VLANS_MASK_REQUIRED("VLAN-82",
        "Mask attribute is required and must be equal or greater than 0"), VLANS_IP_EDIT_NOT_AVAILABLE_PURCHASED(
        "VLAN-83", "Cannot set the IP as 'not available' while it is attached to a virtual machine"),

    // VIRTUAL APPLIANCE
    NON_EXISTENT_VIRTUALAPPLIANCE("VAPP-0", "The requested virtual appliance does not exist"), VIRTUALAPPLIANCE_NOT_DEPLOYED(
        "VAPP-1", "The virtual appliance is not deployed"), VIRTUALAPPLIANCE_NOT_RUNNING("VAPP-2",
        "The virtual appliance is not running"), VIRTUALAPPLIANCE_DEPLOYED("VAPP-3",
        "The virtual appliance is deployed"), VIRTUALAPPLIANCE_INVALID_STATE_DELETE("VAPP-5",
        "The virtual appliance cannot be deleted in this state. It should be NOT_DEPLOYED or UNKNOWN"), VIRTUALAPPLIANCE_MOVE_MISSING_VDC(
        "VAPP-6",
        "The virtual appliance cannot be moved because it has no link to its virtual datacenter"), VIRTUALAPPLIANCE_INVALID_STATE_MOVE(
        "VAPP-8", "The virtual appliance cannot be moved in this state. It should be NOT_DEPLOYED"), VIRTUALAPPLIANCE_INVALID_DC_MOVE_COPY(
        "VAPP-7",
        "The virtual appliance cannot be moved or copied because the target virtual datacenter is not in the same datacenter"), VIRTUALAPPLIANCE_MOVE_COPY_CAPTURED_VM(
        "VAPP-9",
        "The virtual appliance cannot be moved or copied because it contains captured virtual machines"), VIRTUALAPPLIANCE_MOVE_COPY_INCOMPATIBLE_VM(
        "VAPP-10",
        "The virtual appliance cannot be moved or copied because it contains virtual machine templates that are not compatible with the target hypervisor"), VIRTUALAPPLIANCE_COPY_PERSISTENT_VM(
        "VAPP-11",
        "The virtual appliance cannot be copied because it contains persistent virtual machine templates"), VIRTUALAPPLIANCE_EMPTY(
        "VAPP-12", "The virtual appliance does not contain any virtual machines"), VIRTUALAPPLIANCE_INVALID_STATE_COPY(
        "VAPP-13",
        "The virtual appliance cannot be copied in this state. It should be NOT_DEPLOYED"), VIRTUALAPPLIANCE_COPY_LINK(
        "VAPP-14", "The virtual appliance move DTO does not contain the ''original'' link"), VIRTUALAPPLIANCE_MOVE_LINK(
        "VAPP-15", "The virtual appliance move DTO does not contain the ''source'' link"), VIRTUALAPPLIANCE_NON_EXISTENT_LAYER(
        "VAPP-16", "There are no virtual machines in the specified layer"), //
    ANTIAFFINITY_CAPACITY_DC("AAFFINITY-0",
        "The anti-affinity layer requires more machines than currently available in the datacenter"), //
    ANTIAFFINITY_CAPACITY_RACK("AAFFINITY-1",
        "The anti-affinity layer requires more machines than currently available in the rack"), VIRTUALAPPLIANCE_LAYER_LENGHT(
        "VAPP-17", "The length of the layer property must be between 0 and 255 characters"), VIRTUALAPPLIANCE_INVALID_ENTERPRISE_MOVE_COPY(
        "VAPP-18",
        "The virtual appliance cannot be moved or copied because the target virtual datacenter is not in the same enterprise"), VIRTUALAPPLIANCE_INVALID(
        "VAPP-19", "The virtual appliance cannot be created because of validation errors"), VIRTUALAPPLIANCE_UNDEPLOY(
        "VAPP-20", "Error undeploying virtual appliance"), LAYER_NAME_ALREADY_EXIST(
        "LAYER-0",
        "The layer already exists. To add virtual machines to an existing layer, use virtual machine reconfigure"), VIRTUAL_MACHINE_MODIFY_NEW_LAYER(
        "LAYER-1",
        "Cannot create a new layer by reconfiguring the virtual machine. Use the define layer feature"), LAYER_CREATE_2_VM_LINKS(
        "LAYER-2", "Layer creation requires exactly two virtualmachine links"), LAYER_CREATION_VM_ALREADY(
        "LAYER-3",
        "Cannot create layer because one or more of the virtual machines is already associated with a layer"), LAYER_DELETE_EMPTY(
        "LAYER-4",
        "Cannot delete layer because it is associated with more than one virtual machine"), LAYER_INVALID_LINK(
        "LAYER-5", "Malformed layer link"), LAYER_MODIFY_DEPLOYED("LAYER-6",
        "Cannot move a deployed virtual machine from one layer to another"), LAYER_MODIFY_LAST(
        "LAYER-7",
        "Cannot remove association between a layer and one virtual machine. Use the delete layer feature"), LAYER_ADD_DEPLOYED(
        "LAYER-8",
        "Cannot associate the virtual machine with this layer because the virtual machine is already allocated to a hypervisor that is incompatible with the layer"),

    // VIRTUAL CONVERSION
    NON_EXISTENT_VIRTUALAPPLIANCE_STATEFULCONVERSION("VASC-0",
        "The requested persistent conversion does not exist"), INVALID_VASC_STATE("VASC-1",
        "Invalid expected state"),

    // RACK
    NOT_ASSIGNED_RACK_DATACENTER("RACK-0", "The rack is not assigned to the datacenter"), RACK_DUPLICATED_NAME(
        "RACK-3", "There is already a rack with that name in this datacenter"), NON_EXISTENT_RACK(
        "RACK-4", "This rack does not exist"), NON_MANAGED_RACK("RACK-5",
        "Machines in this rack cannot be discovered"), RACK_DUPLICATED_IP("RACK-7",
        "There is already a managed rack with this IP defined in the same datacenter"), RACK_CANNOT_REMOVE_VMS(
        "RACK-9", "Cannot remove this rack because there are some virtual machines deployed on it"), RACK_CONTAINS_DEPLOYED_VMS(
        "RACK-11", "This rack contains deployed virtual machines"), RACK_INVALID_VLAN_RANGE(
        "RACK-12",
        "The new VLAN tag range is not valid because some VLAN tags on the rack are outside of the new range"), RACK_INEXISTENT_VLAN_RANGE(
        "RACK-13",
        "The new VLAN tag range does not contain any available tag, check ''vlanIdMin'', ''vlanIdMax'' and ''vlansIdAvoided''"),

    // MACHINE
    NON_EXISTENT_MACHINE("MACHINE-0", "The requested machine does not exist"), NOT_ASSIGNED_MACHINE_DATACENTER_RACK(
        "MACHINE-1", "The machine is not assigned to the datacenter or rack"), MACHINE_ANY_DATASTORE_DEFINED(
        "MACHINE-2", "Machine definition should have at least one datastore created and enabled"), MACHINE_INVALID_VIRTUAL_SWITCH_NAME(
        "MACHINE-4", "Invalid virtual switch name"), INVALID_STATE_CHANGE("MACHINE-5",
        "The requested state change is not valid"), MACHINE_NOT_ACCESIBLE("MACHINE-6",
        "The requested machine could not be contacted"), MACHINE_CANNOT_BE_DELETED(
        "MACHINE-7",
        "Machine cannot be removed because it is blocked by the high availability engine. Manually re-enable to return it to managed state then try again."), MACHINE_INVALID_IPMI_CONF(
        "MACHINE-8", "Invalid IPMI configuration"), MACHINE_INVALID_IP_RANGE("MACHINE-9",
        "Invalid IP range"), MACHINE_IQN_MISSING("MACHINE-10",
        "The IQN of the target physical machine is not set"), MANAGED_MACHINE_CANNOT_CHANGE_NAME(
        "MACHINE-11", "The machine is in a managed rack and its name cannot be changed."), MACHINE_RESERVED_ENTERPRISE(
        "MACHINE-12",
        "The requested machine cannot be reserved because another enterprise has already reserved it."), MACHINE_NOT_RESERVED(
        "MACHINE-13", "The  machine with ID '%d' cannot be released because it is not reserved"), MACHINE_ALREADY_RESERVED(
        "MACHINE-14", "The requested machine is already reserved."), NOT_VM_ON_MACHINE(
        "MACHINE-15", "There are no virtual machines on the physical machine"), MACHINE_CANNOT_BE_RESERVED_VMS_DEPLOYED(
        "MACHINE-16",
        "The  machine '%s' cannot be reserved because it contains virtual machines deployed by another enterprise."), MACHINE_CAN_NOT_RETRIEVE_VIRTUAL_MACHINES(
        "MACHINE-17",
        "The state of the physical machine does not allow retrieval of virtual machines"), MACHINE_NETWORK_SERVICE_TYPE_INVALID_LINK(
        "MACHINE-18", "Invalid link to network service type"), MACHINE_ALREADY_USED_NST(
        "MACHINE-19", "Duplicate network service type for network interfaces"), MACHINE_USED_NETWORK_INTERFACE(
        "MACHINE-20",
        "Cannot update machine: network service type of network interface cannot be changed when it is in use"), MACHINE_AT_LEAST_ONE_NST(
        "MACHINE-21", "At least one network interface must have a network service type assigned"), MACHINE_USED_NETWORK_INTERFACE_DVS(
        "MACHINE-22",
        "Cannot update machine '%s (%s)': cannot change network service type of network interface '%s' when it is in use"), MACHINE_AND_VIRTUALDATACENTER_INCOMPATIBLES(
        "MACHINE-23",
        "The network service types assigned to the host NICs and the target virtual datacenter are different"), MACHINE_CAN_NOT_CAPTURE_VIRTUAL_MACHINES(
        "MACHINE-24",
        "The state of the physical machine does not allow capture of virtual machines"), MACHINE_DUPLICATED_NI_NAME(
        "MACHINE-25", "Duplicate network interface name"), MACHINE_VSWITCH_22_NOT_INFORMED(
        "MACHINE-26", "Invalid entity: 'virtualSwitch' element not found"), MACHINE_PLUGIN_NOT_IMPLEMENTED(
        "MACHINE-27", "The requested plugin does not provide any host discovery method"), MACHINE_NICS_UNSINCRONIZED(
        "MACHINE-28",
        "There are virtual machines currently not synchronized and it is impossible to recover their current vNICs"), MACHINE_DUPLICATED_NAME_IN_DATACENTER(
        "MACHINE-29", "Machines in the same datacenter must have a unique name"),

    HYPERVISOR_EXIST_IP("HYPERVISOR-1",
        "Invalid hypervisor IP. A hypervisor with that IP already exists"), HYPERVISOR_EXIST_SERVICE_IP(
        "HYPERVISOR-2",
        "Invalid hypervisor service IP. A hypervisor with that service IP already exists"), HYPERVISOR_EXIST_CONNECTION_INFO(
        "HYPERVISOR-3",
        "Invalid hypervisor endpoint information. A hypervisor with that connection already exists"),

    // NETWORK
    NETWORK_INVALID_CONFIGURATION("NET-0",
        "Invalid network configuration for the virtual datacenter"), NETWORK_WITHOUT_IPS("NET-8",
        "This network has no IP addresses"), NETWORK_IP_FROM_BIGGER_THAN_IP_TO("NET-9",
        "Parameter IPFrom is greater than IPTo"), NETWORK_IP_FROM_ERROR("NET-10",
        "Parameter IPFrom is invalid"), NETWORK_IP_TO_ERROR("NET-11", "Parameter IPTo is invalid"), NETWORK_INVALID_CONFIGURATION_LINK(
        "NET-12", "Invalid link for configuring the virtual machine network"), NETWORK_LINK_INVALID_VDC(
        "NET-13", "Invalid virtual datacenter identifier in the configuration link"), NETWORK_LINK_INVALID_VAPP(
        "NET-14", "Invalid virtual appliance identifier in the configuration link"), NETWORK_LINK_INVALID_VM(
        "NET-15", "Invalid virtual machine identifier in the configuration link"), NETWORK_LINK_INVALID_CONFIG(
        "NET-16",
        "Invalid configuration identifier in the configuration link. Configuration ID does not belong to any VLAN configuration used by this virtual machine"), NON_EXISTENT_NETWORK_CONFIGURATION(
        "NET-17", "Nonexistent network configuration"),

    // VIRTUAL MACHINE
    VIRTUAL_MACHINE_WITHOUT_HYPERVISOR("VM-0",
        "The virtual machine does not have a hypervisor assigned"), NON_EXISTENT_VIRTUALMACHINE(
        "VM-1", "The requested virtual machine does not exist"), VIRTUAL_MACHINE_ALREADY_IN_PROGRESS(
        "VM-2", "The virtual machine is already locked by another operation"), VIRTUAL_MACHINE_NOT_DEPLOYED(
        "VM-3", "The virtual machine is not deployed"), VIRTUAL_MACHINE_STATE_CHANGE_ERROR("VM-4",
        "The virtual machine cannot change to the required state"), VIRTUAL_MACHINE_REMOTE_SERVICE_ERROR(
        "VM-5", "The virtual machine cannot change state due to a communication problem"), VIRTUAL_MACHINE_PAUSE_UNSUPPORTED(
        "VM-6", "The virtual machine does not support the action PAUSE"), VIRTUAL_MACHINE_INVALID_STATE_DEPLOY(
        "VM-7", "The allowed state for deploying virtual machines is NOT_ALLOCATED"), VIRTUAL_MACHINE_INVALID_STATE_DELETE(
        "VM-8", "Virtual machine(s) in LOCKED state cannot be deleted"), NON_EXISTENT_VIRTUAL_IMAGE(
        "VM-9", "The requested virtual machine template does not exist"), VIRTUAL_MACHINE_EDIT_STATE(
        "VM-10",
        "The virtual machine is in a state that does not allow the request, therefore it cannot be modified"), VIRTUAL_MACHINE_UNALLOCATED_STATE(
        "VM-11",
        "The virtual machine is not in any hypervisor. Therefore the change of state cannot be applied"), VIRTUAL_MACHINE_INVALID_STATE_UNDEPLOY(
        "VM-12",
        "The allowed power states for virtual machine deployment are ON, OFF, PAUSED, UNKNOWN or ALLOCATED"), VIRTUAL_MACHINE_INCOHERENT_STATE(
        "VM-13",
        "Virtual machine configuration actions can only be performed when the virtual machine is NOT_ALLOCATED or OFF"), VIRTUAL_MACHINE_NETWORK_CONFIGURATION_CAN_NOT_BE_CHANGED(
        "VM-14",
        "Only the 'used' attribute of the virtual machine network configuration can be changed"), VIRTUAL_MACHINE_AT_LEAST_ONE_USED_CONFIGURATION(
        "VM-15",
        "There should be at least one 'used' network configuration in each virtual machine"), VIRTUAL_MACHINE_MACHINE_TEMPLATE_NOT_IN_DATACENTER(
        "VM-16",
        "The virtual machine template supplied is not available in the virtual appliance's datacenter"), VIRTUAL_MACHINE_IMAGE_NOT_COMPATIBLE(
        "VM-18",
        "The virtual machine template is not compatible and there is no compatible conversion"), VIRTUAL_MACHINE_IMAGE_NOT_READY(
        "VM-19",
        "The virtual machine template has a compatible conversion but it is not ready (in progress or failed)"), VIRTUAL_MACHINE_MUST_BE_NON_MANAGED(
        "VM-20", "To perform this action, the virtual machine must be in NON_MANAGED state"), NODE_VIRTUAL_MACHINE_IMAGE_NOT_EXISTS(
        "VM-21", "The virtual machine node does not exist"), VIRTUAL_MACHINE_BACKUP_NOT_FOUND(
        "VM-23",
        "Cannot restore the original virtual machine after a failed reconfigure; the original virtual machine info was not found"), RESOURCE_ALREADY_ASSIGNED_TO_A_VIRTUAL_MACHINE(
        "VM-24", "The resource is already used by another virtual machine"), VIRTUAL_MACHINE_INVALID_STATE_RESET(
        "VM-26", "The allowed power state for resetting a virtual machine is ON"), VIRTUAL_MACHINE_INVALID_STATE_SNAPSHOT(
        "VM-27", "The allowed power state for creating an instance of a virtual machine is OFF"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_MANAGED(
        "VM-28", "Cannot reconfigure a non-managed virtual machine template"), VIRTUAL_MACHINE_RECONFIGURE_NOT_MANAGED(
        "VM-29",
        "Cannot reconfigure the template of a virtual machine that is not managed by the platform"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_NOT_SAME_MASTER(
        "VM-30",
        "Cannot reconfigure a virtual machine (instance or persistent) to change its template to another master"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_ATTACHED_PRESISTENT(
        "VM-31",
        "The persistent virtual machine template supplied for reconfigure is already attached to a virtual machine"), VIRTUAL_MACHINE_RECONFIGURE_TEMPLATE_IN_THE_HYPERVISOR(
        "VM-32",
        "Cannot reconfigure the virtual machine template when the virtual machine is present in the hypervisor"), VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE(
        "VM-33", "We do not currently allow imported virtual machines to be reconfigured"), VIRTUAL_MACHINE_IMPORTED_CAN_NOT_RECONFIGURE_FULLY(
        "VM-34", "Only 'cpu' and 'ram' can be reconfigured in imported virtual machines"), VIRTUAL_MACHINE_IMPORTED_WILL_BE_DELETED(
        "VM-44",
        "You are trying to undeploy an imported virtual machine. If you undeploy it, the virtual machine template cannot be recovered. To proceed with the undeploy, please call this functionality again with the 'forceUndeploy=true' option"), RESOURCES_ALREADY_ASSIGNED(
        "VM-45", "Some of the resources indicated are already used"), VIRTUAL_MACHINE_AT_LEAST_ONE_NIC_SHOULD_BE_LINKED(
        "VM-46",
        "At least one IP address must be supplied when changing virtual machine NICs in deployed machine"), VIRTUAL_MACHINE_AT_LEAST_ONE_DISK_SHOULD_BE_LINKED(
        "VM-47",
        "At least one hard disk must be supplied using a link when changing virtual machine hard disks"), VIRTUAL_MACHINE_DISK_ALREADY_ATTACHED_TO_THIS_VIRTUALMACHINE(
        "VM-48", "Disk already attached to this virtual machine"), VIRTUAL_MACHINE_MAX_NICS_ALLOWED(
        "VM-49", "You have exceeded the number of NICs that can be added to a virtual machine"), VIRTUAL_MACHINE_TWO_NICS_SAME_VLAN_NOT_ALLOWED(
        "VM-50", "You are not permitted to add two NICs with IPs in the same VLAN"), VIRTUAL_MACHINE_CREATION_PERSISTENT_NOT_FINISHED(
        "VM-51",
        "Cannot create a virtual machine because the persistent virtual machine template is not ready"), SNAPSHOT_CONSTRAINT_LENGTH_NAME_MIN(
        "CONSTR-SNAPSHOT-LENGTH-NAME-MIN", "The length of the NAME parameter must be greater than "
            + VirtualMachineTemplate.NAME_LENGTH_MIN + " characters"), SNAPSHOT_CONSTRAINT_LENGTH_NAME_MAX(
        "CONSTR-SNAPSHOT-LENGTH-NAME-MAX", "The length of the NAME parameter must be less than "
            + VirtualMachineTemplate.NAME_LENGTH_MAX + " characters"), VIRTUAL_MACHINE_NO_NODE(
        "VM-52",
        "The current virtual machine is not assigned to a virtual appliance, so cannot obtain its representation using media-type "
            + VirtualMachineWithNodeDto.TYPE + " or " + VirtualMachineWithNodeExtendedDto.TYPE), SNAPSHOT_CONSTRAINT_NAME(
        "CONSTR-InstanceName", "Missing instance name"), ESXI_4MULTIPLE("VM-53",
        "ESXi only supports memory sizes that are a multiple of 4"), VIRTUAL_MACHINE_DISK_SEQUENCE_REPETITION(
        "VM-54",
        "Virtual machine contains extra HardDisk and/or Volumes with the same sequence, try detaching some external storage."), VIRTUAL_MACHINE_IDE_CONTROLLER_FULL(
        "VM-55", "Cannot attach more than 4 disks to an IDE controller"), VIRTUAL_MACHINE_DEFINITION_INVALID(
        "VM-56",
        "Invalid virtual machine description: missing primary disk, hardware profile or identifier."), VIRTUAL_MACHINE_HYPERVISOR_NOT_MANAGED(
        "VM-57",
        "The hypervisor where the virtual machine is deployed is not running properly. Contact your System Administrator"), VIRTUAL_MACHINE_IP_NOT_AVAILABLE(
        "VM-58",
        "There are no IP addresses available in the default network for use by a virtual machine."), VIRTUAL_MACHINE_RECONFIGURE_UNEXPECTED_ERRORS(
        "VM-59",
        "Virtual machine cannot be reconfigured due to unexpected errors. Contact your System Administrator"), VIRTUAL_MACHINE_DEPLOYED_CANT_MOVE_LAYER(
        "VM-60",
        "Cannot change the layer of a virtual machine that has been assigned to a hypervisor"), VIRTUAL_MACHINE_RECONFIGURE_MISSING_DISK_TYPE(
        "VM-61", "The disk link(s) supplied are missing the type attribute or it is invalid"), VM_DISK_REL_REPEATED_LINKS(
        "VM-62", "Invalid input. Repeated disk attachment value is not allowed."), VM_DISK_HREF_REPEATED_LINKS(
        "VM-63", "Invalid input. Repeated disk href value is not allowed."), VM_INSTANCE_OF_NOT_MANAGED(
        "VM-64",
        "Cannot create an instance of a virtual machine that is not managed by the platform. Please capture the virtual machine then try again"), VM_NODE_NAME_NOT_FOUND(
        "VM-65", "The name of the node is required"), VIRTUAL_MACHINE_DEPLOY_NO_NST(
        "VM-66",
        "Cannot deploy the virtual machine because target physical NIC cannot be found. The physical NIC where the virtual NIC with IP was attached may have been removed directly in the hypervisor "), VIRTUAL_MACHINE_LINK_NOT_FOUND(
        "VM-67", "Virtual machine link not found"), VIRTUAL_MACHINE_INVALID_LINK("VM-68",
        "Malformed virtual machine link"), VM_RECONFIGURE_INVALID_NST("VM-69",
        "Current target hypervisor does not have the required physical NICs to allocate the requested IPs"), VIRTUAL_MACHINE_PRIMARY_DISK_CANNOT_BE_MODIFIED(
        "VM-70", "The primary disk of the virtual machine cannot be modified"), VIRTUAL_MACHINE_PRIMARY_DISK_SHOULD_BE_DEFINED(
        "VM-71", "At least one primary disk must be defined."), VIRTUAL_MACHINE_PRIMARY_DISK_SHOULD_BE_UNIQUE(
        "VM-72", "Primary disk of virtual machine should be unique."), VIRTUAL_MACHINE_VDRP_PASSWORD_TOO_LARGE(
        "VM-73", "VRDP passwords should be up to 8 characters"), VIRTUAL_MACHINE_VDRP_PASSWORD_INVALID_CHAR(
        "VM-74",
        "Invalid character in password. VRDP password characters should be in range [a..z][A..Z][0..9]"), VIRTUAL_MACHINE_DUPLICATED_VLAN(
        "VM-75", "The Virtual Machine has two NICs using IPs in the same VLAN"), VIRTUAL_MACHINE_UPDATE_RESOURCES(
        "VM-76", "Cannot update the resources in target physical machine and datastore "),

    VM_CAPTURE_LINK_REQUIRED("VM-77",
        "To capture a virtual machine, the virtualmachine link from the infrastructure context is required"), VM_CAPTURE_INVALID_LINK(
        "VM-78", "Malformed virtual machine link from infrastructure to capture"), VM_CAPTURE_INVALID_VDC_TYPE(
        "VM-79",
        "The virtual datacenter is not compatible with the hypervisor from which you are capturing the virtual machine"), VM_CAPTURE_DISK_SEQUENCE_REPEATED(
        "VM-80", "Disk sequences cannot be repeated"), VM_CAPTURE_DISK_SEQUENCE_0_NOT_SELECTABLE(
        "VM-81",
        "The disk at sequence 0 is captured by default, so it is not necessary to define it"), VM_CAPTURE_DISK_SEQUENCE_DOES_NOT_MATCH(
        "VM-82", "The disk sequence does not match the data retrieved from the hypervisor"), VM_CAPTURE_DISK_SIZE_DOES_NOT_MATCH(
        "VM-83", "The disk size does not match the data retrieved from the hypervisor"), VM_CAPTURE_DISK_PROPERTIES_REQUIRED(
        "VM-84", "The size and the sequence are required properties of the disk"), VM_CAPTURE_REMOTE_DISK_NOT_CAPTURED(
        "VM-85", "Some disks on the remote virtual machine cannot be captured"), VM_CAPTURE_VLAN_REQUIRED_BY_NIC(
        "VM-86", "Every NIC defined requires a VLAN link"), VM_CAPTURE_INVALID_VLAN_LINK("VM-87",
        "Malformed VLAN link"), VM_CAPTURE_INVALID_VLAN_LINK_VDC("VM-88",
        "The VLAN link contains an invalid virtual datacenter"), VM_CAPTURE_INVALID_VLAN_LINK_ENTERPRISE(
        "VM-89", "The VLAN link contains an invalid enterprise"), VM_CAPTURE_INVALID_VLAN_LINK_DATACENTER(
        "VM-90", "The VLAN link contains an invalid datacenter"), VM_CAPTURE_IP_SEQUENCE_REPEATED(
        "VM-91", "IP sequence cannot be repeated"), VM_CAPTURE_IP_ALREADY_ATTACHED("VM-92",
        "The selected IP is already attached to another virtual machine"), VM_CAPTURE_IP_INVALID_ADDRESS(
        "VM-93", "Malformed IP address"), VM_CAPTURE_IP_SEQUENCE_NOT_MATCH(
        "VM-94",
        "The IP from the remote virtual machine does not match the NIC defined by the user in the same sequence"), VM_CAPTURE_IP_MAC_NOT_MATCH(
        "VM-95",
        "The IP from the remote virtual machine does not match the NIC defined by the user with the same MAC address"), VM_CAPTURE_IP_PROPERTIES_REQUIRED(
        "VM-96",
        "The MAC address, the IP address and the sequence are required properties of the NIC"), VM_CAPTURE_REMOTE_IP_NOT_CAPTURED(
        "VM-97", "There are some NICs on the remote machine that cannot be captured"), VM_CAPTURE_IP_NOT_PURCHASED(
        "VM-98", "The public IP was not purchased for this virtual datacenter"), VM_CAPTURE_IP_QUARENTINE(
        "VM-99", "The requested IP is in quarantine"), VM_CAPTURE_IP_NOT_AVAILABLE("VM-100",
        "The requested IP is not available"), VM_CAPTURE_NST_INVALID(
        "VM-101",
        "The network service type of the VLAN does not match the network service type of the selected network interface in the hypervisor"), VM_CAPTURE_INVALID_VLAN_TAG(
        "VM-102",
        "The selected remote IP is using a tag that is incompatible with the selected VLAN network"), VM_CAPTURE_NOT_AVAILABLE_VLAN_TAG(
        "VM-103",
        "The selected remote IP is using a tag that is already used by a different VLAN than the selected one"), VM_CAPTURE_VLAN_TAG_OUT_OF_RACK_RANGE(
        "VM-104", "Cannot assign the VLAN tag to the selected VLAN because it is out of rack range"), VM_CAPTURE_VLAN_TAG_RACK_FULL(
        "VM-105", "The selected rack will not allow new VLAN tags"),

    VM_RELEASE_MUST_BE_CAPTURED("VM-106",
        "A virtual machine must be captured in order to be released"), VM_RELEASE_MUST_BE_DEPLOYED(
        "VM-107", "A virtual machine must be deployed in order to be released"), VIRTUAL_MACHINE_RECONFIGURE_INVALID_NEW_VALUES(
        "VM-108", "Virtual machine cannot be reconfigured because of invalid new values"), VIRTUAL_MACHINE_USING_MISSING_FILE_TEMPLATE_WILL_BE_DELETED(
        "VM-109",
        "You are trying to undeploy a virtual machine using a template that is not stored in the Apps Library. If you undeploy it, the virtual machine template cannot be recovered. To proceed with the undeploy, please call this functionality again with the 'forceUndeploy=true' option"), VIRTUAL_MACHINE_DEPLOY_MISSING_FILE_TEMPLATE(
        "VM-110",
        "Virtual machine is using a template without a system disk file. It cannot be deployed."), VIRTUAL_MACHINE_CREATE_MISSING_FILE_TEMPLATE(
        "VM-111",
        "Virtual machine cannot be created from a virtual machine template without a disk file"), CORES_PER_SOCKET_UNSUPPORTED(
        "VM-112",
        "Cannot perform this action because the cores per socket setting is not available for this hypervisor"), VIRTUAL_MACHINE_CREATE_TEMPLATE_NOT_DONE(
        "VM-113",
        "Virtual machine cannot be created from a virtual machine template that is not in the DONE state; check the state and pending tasks"), VIRTUAL_MACHINE_UPDATE_HD_IN_BYTES(
        "VM-114", "Cannot modify ''hdInBytes'' of virtual machine; resize the primary disk instead"), VM_CAPTURE_VLAN_STRICT(
        "VM-115", "Cannot select an IPv6 strict network to capture a virtual machine"), VIRTUAL_MACHINE_RECONFIGURE_PROVIDER_NOT_IMPLEMENTS_VMMETRICS_INTERFACE(
        "VM-116",
        "The provider of the given virtual machine does not have support for virtual machine metrics. The monitoring flag cannot be modified."),

    // ROLE
    NON_EXISTENT_ROLE("ROLE-0", "The requested role does not exist"), NON_MODIFICABLE_ROLE(
        "ROLE-1", "The requested role cannot be modified"), PRIVILEGE_PARAM_NOT_FOUND("ROLE-2",
        "Missing privilege parameter"), DELETE_ERROR("ROLE-3",
        "The requested role is blocked. It cannot be deleted"), DELETE_ERROR_WITH_USER("ROLE-4",
        "Cannot delete a role with a user associated"), DELETE_ERROR_WITH_ROLE_LDAP("ROLE-5",
        "Cannot delete a role with a RoleLdap associated"), DUPLICATED_ROLE_NAME_ENT(
        "ROLE-6",
        "Cannot create a role with the same name as an existing role for the same enterprise or with the same name as an existing global role"), DUPLICATED_ROLE_NAME_GEN(
        "ROLE-7", "Cannot create a global role with the same name as an existing role"), HAS_NOT_ENOUGH_PRIVILEGE(
        "ROLE-8", "Not enough privileges to manage this role"), ROLE_NAME_BLANK("ROLE-9",
        "Property name must not be blank"), NON_MODIFICABLE_ROLE_ENTERPRISE(
        "ROLE-10",
        "Cannot modify the enterprise of this role because it is currently assigned to users of that enterprise."),

    // PRIVILEGE
    NON_EXISTENT_PRIVILEGE("PRIVILEGE-0", "The requested privilege does not exist"),

    // ROLE_LDAP
    NON_EXISTENT_ROLELDAP("ROLELDAP-0", "The requested roleLdap does not exist"), MULTIPLE_ENTRIES_ROLELDAP(
        "ROLELDAP-1", "There are multiple entries for the requested roleLdap"), NOT_ASSIGNED_ROLE(
        "ROLELDAP-2", "The roleLdap must have a role"),

    // USER
    NOT_ASSIGNED_USER_ENTERPRISE("USER-0", "The user is not assigned to the enterprise"), MISSING_ROLE_LINK(
        "USER-1", "Missing link to the role"), ROLE_PARAM_NOT_FOUND("USER-2",
        "Missing roles parameter"), USER_NON_EXISTENT("USER-3", "The requested user does not exist"), USER_DUPLICATED_NICK(
        "USER-4", "Duplicate username for user"), EMAIL_IS_INVALID("USER-5",
        "The email is not valid"), NOT_USER_CREACION_LDAP_MODE("USER-6",
        "In LDAP mode cannot create user"), NOT_EDIT_USER_ROLE_LDAP_MODE("USER-7",
        "In LDAP mode cannot modify user's role"), NOT_EDIT_USER_ENTERPRISE_LDAP_MODE("USER-8",
        "In LDAP mode cannot modify user's enterprise"), USER_DELETING_HIMSELF("USER 9",
        "The user cannot delete his own user account"), USER_NICK_CANNOT_BE_CHANGED("USER 10",
        "Cannot change the user nick (username)"), USER_PASSWORD_IS_NECESSARY("USER 11",
        "The user password is required"), USER_NAME_IS_NECESSARY("USER 12",
        "The user name is required"), USER_NICK_IS_NECESSARY("USER 13",
        "The user nick (username) is required"), USER_VDC_RESTRICTED("USER 14",
        "Your enterprise does not allow you to manage this virtual datacenter"), NON_EXISTENT_SESSION(
        "USER-15", "The supplied session does not exist"), DELETE_USER_ROLE_IS_BLOCKED("USER-16",
        "The role of the user is locked. Only users with the same role can delete users with locked roles"), USER_LOCAL_ROLE(
        "USER-17", "Cannot move user with local role to another enterprise"), USER_CANNOT_ASSOCIATE_ROLE(
        "USER-18",
        "Cannot assign role to user because the user's enterprise is not visible in the role's scope"), USER_SCOPE_GREATER(
        "USER-19",
        "Cannot assign role to user because the role's scope is greater than the current user's scope."), USER_AND_ROLE_MUST_BELONG_SAME_ENT(
        "USER-20", "User must have a role from his enterprise or a global role"), USER_SURNAME_IS_NECESSARY(
        "USER-21", "The field surname is required"), MODIFY_USER_ROLE_IS_BLOCKED("USER-22",
        "The role of the user is locked. Only users with the same role can modify users with locked roles"), NON_EXISTENT_APPLICATION(
        "USER-23", "The requested application does not exist"), USER_VDC_DIFFERENT_ENTERPRISE(
        "USER-24", "The virtual datacenter does not belong to the user's enterprise"),

    // REMOTE SERVICE
    NOT_ASSIGNED_REMOTE_SERVICE_DATACENTER("RS-0",
        "The remote service is not assigned to the datacenter"), WRONG_REMOTE_SERVICE_TYPE("RS-1",
        "Wrong remote service type"), NON_EXISTENT_REMOTE_SERVICE_TYPE("RS-2",
        "The remote service does not exist"), REMOTE_SERVICE_URL_ALREADY_EXISTS("RS-3",
        "The remote service URL already exists and cannot be duplicated"), REMOTE_SERVICE_MALFORMED_URL(
        "RS-4", "Malformed remote service URL"), REMOTE_SERVICE_POOL_ASIGNED("RS-5",
        "This datacenter already has a storage pool assigned"), REMOTE_SERVICE_TYPE_EXISTS("RS-6",
        "This datacenter already has a remote service of that type"), REMOTE_SERVICE_CONNECTION_FAILED(
        "RS-7", "Failed connection to the remote service"), REMOTE_SERVICE_CANNOT_BE_CHECKED(
        "RS-8", "This remote service is not available to be checked"), APPLIANCE_MANAGER_REPOSITORY_ALREADY_DEFINED(
        "RS-AM-0",
        "The repository exported by the current appliance manager is being used in another datacenter"), APPLIANCE_MANAGER_REPOSITORY_IN_USE(
        "RS-AM-1",
        "The current repository holds virtual machine templates being used in some virtual appliances, so it is not possible to remove this remote service. You can replace the appliance manager with another one but only if the same repository is used."), REMOTE_SERVICE_STORAGE_REMOTE_WITH_POOLS(
        "RS-9", "Cannot delete a storage manager with storage pools associated"), REMOTE_SERVICE_DHCP_IS_BEING_USED(
        "RS-10", "Cannot delete a DHCP Service. There are virtual machines deployed."), REMOTE_SERVICE_WRONG_URL(
        "RS-11", "The URL supplied is invalid"), REMOTE_SERVICE_DHCP_WRONG_URI("RS-12",
        "The URI of the DHCP remote service is invalid"), REMOTE_SERVICE_DATACENTER_UUID_NOT_FOUND(
        "RS-13", "The remote service does not have the *abiquo.datacenter.id* property set"), REMOTE_SERVICE_DATACENTER_UUID_INCONSISTENT(
        "RS-14",
        "The remote service is configured with a different datacenter UUID, please adjust the *abiquo.datacenter.id* property in the remote service."), REMOTE_SERVICE_UNDEFINED_PORT(
        "RS-15", "A port must be defined in the URI"), REMOTE_SERVICE_NON_POOLABLE("RS-16",
        "The remote service indicated cannot be used in a remote service client pool"), REMOTE_SERVICE_ERROR_BORROWING(
        "RS-17",
        "An unexpected error occurred while getting the remote service client from the client pool"), REMOTE_SERVICE_OWN_EXCEPTION(
        "RS-18", "Remote service exception: the remote service log contains more details"), REMOTE_SERVICE_VSM_IS_BEING_USED(
        "RS-19",
        "Cannot delete a virtual system monitor service. There are virtual machines deployed."), REMOTE_SERVICE_URI_REQUIRED(
        "RS-20", "The URI parameter is required"), DATACENTER_UUID_DUPLICATED("RS-21",
        "Datacenter UUID already used"), REMOTE_SERVICE_DHCPv6_WRONG_URI("RS-22",
        "The URI of the DHCPv6 remote service is invalid"), NOT_ASSIGNED_REMOTE_SERVICE_REGION(
        "RS-23", "The remote service is not assigned to the public cloud region"), REMOTE_SERVICE_TYPE_REQUIRED(
        "RS-24", "The type parameter is required"),
    //
    AM_CLIENT("AM-0", "Failed appliance manager communication"), AM_TIMEOUT("AM-1",
        "Timeout during appliance manager communication"), AM_UNAVAILABE("AM-2",
        "AM service unavailable; please check the URL of the service."), AM_FAILED_REQUEST(
        "AM-3",
        "Failed appliance manager request. "
            + "It is possible that the repositoryLocation property is not correct, NFS is not available or NFS privileges do not allow access to the server."), REPOSITORY_PUBLIC_CLOUD_ACTIONS(
        "REPO-1", "Cannot request ''usage'' or ''refresh'' of a public cloud repository"), AM_FAILED_REQUEST_NOTFOUND(
        "AM-4",
        "Template file was not found. "
            + "It is possible that the repository is not properly refreshed; request a refresh or wait for the periodic repository check."),

    // OVF PACKAGE LIST
    TEMPLATE_DEFINITION_LIST_NAME_ALREADY_EXIST("OVF-PACKAGE-LIST-0",
        "OVF Package list name already exists"), //
    TEMPLATE_DEFINITION_LIST_REFRESH_NO_URL(
        "OVF-PACKAGE-LIST-1",
        "The template definition list is not associated with a URL (ovfindex.xml), so it cannot be refreshed from the source"), //
    TEMPLATE_DEFINITION_LIST_NAME_NOT_FOUND("OVF-PACKAGE-LIST-2",
        "OVF Package list name is required"),

    // OVF PACKAGE
    NON_EXISTENT_OVF_PACKAGE("OVF-PACKAGE-0", "The requested OVF package does not exist"), NON_EXISTENT_TEMPLATE_DEFINITION_LIST(
        "OVF-PACKAGE-1", "The requested OVF package list does not exist"), OVF_PACKAGE_CANNOT_TRANSFORM(
        "OVF-PACKAGE-2", "Cannot return the template definition"), INVALID_OVF_INDEX_XML(
        "OVF-PACKAGE-3", "Cannot find the RepositorySpace"), NON_EXISTENT_REPOSITORY_SPACE(
        "OVF-PACKAGE-4", "The requested RepositorySpace does not exist"), INVALID_DISK_FORMAT_TYPE(
        "OVF-PACKAGE-5", "The URL of the disk format type is invalid"), INVALID_TEMPLATE_OVF_URL(
        "OVF-PACKAGE-6", "The OVF URL in the template definition is invalid"),

    // VIRTUAL MACHINE TEMPLATE
    VIMAGE_INVALID_ALLOCATION_UNITS("VIMAGE-INVALID-OVF-ALLOCATION-INITS",
        "Virtual machine template cannot be added due to invalid allocation units"), VMTEMPLATE_SYNCH_DC_REPO(
        "VIMAGE-SYNCH-DATACENTER-REPOSITORY",
        "Cannot obtain downloaded templates in the datacenter repository"), VIMAGE_DATACENTER_REPOSITORY_NOT_FOUND(
        "DATACENTER-REPOSITORY-NOT-CREATED",
        "Datacenter repository not configured; check the appliance manager of the datacenter. Contact your Infrastructure Administrator"), VMTEMPLATE_REPOSITORY_CHANGED(
        "VIMAGE-REPOSITORY-CHANGED", "Datacenter repository location has changed"), VIMAGE_AM_DOWN(
        "VIMAGE-AM-DOWN", "Check appliance manager configuration error"), NON_EXISTENT_VIRTUAL_MACHINE_TEMPLATE(
        "VIMAGE-0", "The requested virtual machine template does not exist"), VIMAGE_IS_NOT_BUNDLE(
        "VIMAGE-1", "The virtual machine template supplied is not an instance"), INVALID_VMTEMPLATE_LINK(
        "VIMAGE-2",
        "Invalid virtual machine template identifier in the virtual machine template link"), INVALID_DATACENTER_RESPOSITORY_LINK(
        "VIMAGE-3", "Invalid datacenter repository identifier in the datacenter repository link"), VMTEMPLATE_ENTERPRISE_CANNOT_BE_CHANGED(
        "VIMAGE-4", "Cannot change the enterprise of the virtual machine template"), VMTEMPLATE_DATACENTER_REPOSITORY_CANNOT_BE_CHANGED(
        "VIMAGE-5", "Cannot change the datacenter repository of a virtual machine template"), VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_CHANGED(
        "VIMAGE-6", "Cannot change the master template of a virtual machine template"), VMTEMPLATE_MASTER_TEMPLATE_CANNOT_BE_DELETED(
        "VIMAGE-7",
        "The requested virtual machine template is a master template; master templates cannot be deleted"), VMTEMPLATE_SHARED_TEMPLATE_FROM_OTHER_ENTERPRISE(
        "VIMAGE-9",
        "Cannot delete the requested shared virtual machine template because it belongs to another enterprise"), VMTEMPLATE_TEMPLATE_USED_BY_VIRTUAL_MACHINES_CANNOT_BE_DELETED(
        "VIMAGE-10",
        "The virtual machine template is being used by virtual machines and cannot be deleted"), VMTEMPLATE_TEMPLATE_USED_BY_VIRTUAL_MACHINES_CANNOT_BE_UNSHARED(
        "VIMAGE-11",
        "The virtual machine template is being used by virtual machines in other enterprises and cannot be modified to not shared"), VIMAGE_MALFORMED_ICON_URI(
        "VIMAGE-12", "Malformed icon URL"), VMTEMPLATE_IS_NOT_STATEFUL("VIMAGE-13",
        "The virtual machine template is not a persistent template"), VMTEMPLATE_FAILED_PERSISTENT_CANNOT_BE_MODIFIED(
        "VIMAGE-14", "Cannot modify a failed persistent virtual machine template"), VMTEMPLATE_IN_PROGRESS_PERSISTENT_CANNOT_BE_MODIFIED(
        "VIMAGE-15", "Cannot modify a persistent virtual machine template that is in progress"), TEMPLATE_DEFINITION_LINK_FOUND(
        "VIMAGE-16", "No template definition link found"), TEMPLATE_DEFINITION_LINK_INVALID(
        "VIMAGE-17", "Invalid template definition link"), VMTEMPLATE_ALREADY_DOWNLOADED(
        "VIMAGE-18",
        "The requested template is already present for the current enterprise in the datacenter provided."), TEMPLATE_DELETE_STATEFUL_PROGRESS(
        "VIMAGE-19", "A persistent virtual machine template that is in progress cannot be deleted"), VMTEMPLATE_PERSISTENT_DISK_TYPE_IMMUTABLE(
        "VIMAGE-20", "The disk type of a persistent virtual machine template cannot be modified"), VMTEMPLATE_PERSISTENT_CANNOT_BE_SHARED(
        "VIMAGE-21", "A persistent virtual machine template cannot be shared"), VMTEMPLATE_PERSISTENT_PATH_IMMUTABLE(
        "VIMAGE-22", "The path of a persistent virtual machine template cannot be modified"), VMTEMPLATE_PERSISTENT_HD_REQUIRED_IMMUTABLE(
        "VIMAGE-23",
        "The HD required of a persistent virtual machine template cannot be modified. Resize the primary disk instead"), VMTEMPLATE_PERSISTENT_DISK_FILE_SIZE_IMMUTABLE(
        "VIMAGE-24",
        "The disk file size of a persistent virtual machine template cannot be modified"), VMTEMPLATE_LINK_NOT_FOUND(
        "VIMAGE-25", "Missing virtual machine template link"), VMTEMPLATE_LINK_INVALID("VIMAGE-26",
        "Invalid virtual machine template link"), VMTEMPLATE_INVALID_CREATE_REQUEST(
        "VIMAGE-27",
        "The virtual machine template creation request must include a template definition link OR a virtual machine template link of an instance."), VMTEMPLATE_PROMOTE_CONVERSION_IN_PROGRESS(
        "VIMAGE-28",
        "Cannot promote a virtual machine template instance with QUEUED conversions; please wait for them to finish then try again."), VMTEMPLATE_FROM_OTHER_ENTERPRISE_NOT_SHARED(
        "VIMAGE-29",
        "The virtual machine template belongs to another enterprise and is not a shared template."), TEMPLATE_DELETE_CONVERSION_PROGRESS(
        "VIMAGE-30", "Cannot delete virtual machine template because conversions are in progress."), TEMPLATE_DELETE_INSTANCE_PROGRESS(
        "VIMAGE-31",
        "Cannot delete instance of virtual machine template because a conversion of this instance is still in progress."), TEMPLATE_DELETE_FILE_STATEFUL(
        "VIMAGE-33", "Cannot delete the disk file of a persistent virtual machine template"), TEMPLATE_DELETE_FILE_USE_DELETE(
        "VIMAGE-34",
        "Current virtual machine template is not being used by any deployed virtual machines and it has no instances. Use template delete"), TEMPLATE_FILE_NOT_FOUND(
        "VIMAGE-35",
        "Virtual machine template disk file is not found or currently unavailable in the repository filesystem."), TEMPLATE_CREATE_IN_REPO_ERROR(
        "VIMAGE-36",
        "Cannot process a virtual machine template creation event from datacenter repository refresh check."), TEMPLATE_DELETE_UNAVAILABLE(
        "VIMAGE-37", "Current virtual machine template already unavailable"), VMTEMPLATE_PUBLIC_PATH_IMMUTABLE(
        "VIMAGE-38", "The path of a public cloud template cannot be modified"), VMTEMPLATE_PUBLIC_DUPLICATED_PATH(
        "VIMAGE-39", "A public cloud template already exists for this path identifier"), VMTEMPLATE_PUBLIC_SHARED_IMMUTABLE(
        "VIMAGE-40", "A public cloud template cannot be shared"), VMTEMPLATE_PUBLIC_DISKFORMAT_COMPATIBLE(
        "VIMAGE-41",
        "The disk format type of a public cloud template must be compatible with the provider"),

    CONVERSION_NO_FORMAT(
        "CONVERSION-0",
        "Invalid DiskFormat for the conversion; allowed values"
            + ": RAW, VMDK_STREAM_OPTIMIZED, VMDK_FLAT, VMDK_SPARSE, VHD_FLAT, VHD_SPARSE, VDI_FLAT, VDI_SPARSE, QCOW2_FLAT, QCOW2_SPARSE"), CONVERSION_NOT_FOUND(
        "CONVERSION-1", "The requested conversion does not exist"), CONVERSION_ALREADY_EXIST(
        "CONVERSION-2",
        "The requested conversion already exists for the given virtual machine template and disk format"), CONVERSION_TO_TEMPLATE_FORMAT(
        "CONVERSION-3",
        "The requested conversion format is the same as the base format of the virtual machine template"), CONVERSION_RESTART_ONLY_FAILED(
        "CONVERSION-4", "Could not restart the requested conversion because it had not failed"), CONVERSION_ENQUEUED_NO_TASK(
        "CONVERSION-5",
        "Conversion state is QUEUED but it does not have an associated task; try deleting the conversion before retrying."), CONVERSION_FAILED_SEND(
        "CONVERSION-6", "Failed to send conversion of template."), CONVERSION_UNKNOWN_FORMAT(
        "CONVERSION-7",
        "Cannot request a conversion of a virtual machine template with UNKNOWN or INCOMPATIBLE disk format."),

    // NODE COLLECTOR
    NON_EXISTENT_IP("NC-0", "The requested IP does not exist"), MISSING_IP_PARAMETER("NC-1",
        "Missing IP query parameter"), NC_BAD_CREDENTIALS_TO_RACK("NC-2",
        "Bad credentials attempting to retrieve the list of physical machines from rack"), NC_BAD_CREDENTIALS_TO_MACHINE(
        "NC-3", "Bad credentials attempting to retrieve the machine "), NC_CONNECTION_EXCEPTION(
        "NC-4", "There is a machine running at the given IP but no hypervisor is responding"), NC_NOT_FOUND_EXCEPTION(
        "NC-5", "There is no machine running at the given IP"), NC_UNEXPECTED_EXCEPTION("NC-6",
        "Unexpected exception building the request to discovery manager"), NC_UNAVAILABLE_EXCEPTION(
        "NC-7", "The discovery manager is currently not available"), NC_VIRTUAL_MACHINE_NOT_FOUND(
        "NC-8", "The requested virtual machine was not found on the remote hypervisor"), NC_NOT_MANAGED_HOST(
        "NC-9", "The requested host is not managed"), NC_INVALID_IP("NC-10",
        "The IP format is invalid"), NC_BAD_CONFIGURATION(
        "NC-11",
        "Access is denied, please check whether the [domain-username-password] are correct. Also, if not already done please check the GETTING STARTED and FAQ sections in readme.htm. They provide information on how to correctly configure the Windows machine for DCOM access, so as to avoid such exceptions. "),

    // STORAGE POOL
    MISSING_REQUIRED_QUERY_PARAMETER_IQN("SP-1", "Missing IQN query parameter"), CONFLICT_STORAGE_POOL(
        "SP-2", "The ID of the storage pool and the ID of the object supplied must be the same"), NON_EXISTENT_STORAGE_POOL(
        "SP-3", "The requested storage pool does not exist"), STORAGE_POOL_ERROR_MODIFYING("SP-4",
        "There was an unexpected error while modifying the storage pool"), STORAGE_POOLS_SYNC(
        "SP-5", "Could not get the storage pools from the target device"), STORAGE_POOL_SYNC(
        "SP-6", "Could not get the requested storage pool from the target device"), CONFLICT_VOLUMES_CREATED(
        "SP-7", "Cannot edit or delete the storage pool because it contains volumes"), STORAGE_POOL_DUPLICATED(
        "SP-8", "Duplicate storage pool"), STORAGE_POOL_TIER_IS_DISABLED("SP-9", "Tier is disabled"), STORAGE_POOL_PARAM_NOT_FOUND(
        "SP-10", "Missing storage pool parameter"), STORAGE_POOL_LINK_DATACENTER_PARAM_NOT_FOUND(
        "SP-11", "The datacenter parameter was not found in the storage pool link"), STORAGE_POOL_LINK_DEVICE_PARAM_NOT_FOUND(
        "SP-12", "The storage device parameter was not found in the storage pool link"), MISSING_POOL_LINK(
        "SP-13", "Missing storage pool link"), STORAGE_POOL_TIER_IS_RESTRICTED("SP-14",
        "Access to this tier is restricted for your enterprise"), STORAGE_POOL_SAME_AS_AM("SP-15",
        "The storage pool cannot be added because it is the NFS repository of the Appliance Manager"),

    // DATASTORE
    DATASTORE_NON_EXISTENT("DATASTORE-0", "The requested datastore does not exist"), DATASTORE_DUPLICATED_NAME(
        "DATASTORE-4", "Duplicate name for the datastore"), DATASTORE_DUPLICATED_ROOTPATH(
        "DATASTORE-5", "Duplicate root path for the datastore"), DATASTORE_DUPLICATED_DIRECTORY(
        "DATASTORE-6", "Duplicate directory path for the datastore"), DATASTORE_NOT_ASSIGNED_TO_MACHINE(
        "DATASTORE-7", "The datastore is not assigned to that machine"), DATASTORE_LINK_NOT_FOUND(
        "DATASTORE-8", "Missing datastore link"), DATASTORE_INVALID_LINK("DATASTORE-9",
        "Invalid datastore link"),

    // SYSTEM PROPERTIES
    NON_EXISTENT_SYSTEM_PROPERTY("SYSPROP-0", "The requested system property does not exist"), SYSTEM_PROPERTIES_DUPLICATED_NAME(
        "SYSPROP-1", "There is already a system property with that name"),

    // ALLOCATOR
    LIMITS_EXCEEDED("LIMIT-1", "The required resources exceed the allowed limits"), SOFT_LIMIT_EXCEEDED(
        "LIMIT-2", "The required resources exceed the soft limits"), NOT_ENOUGH_RESOURCES(
        "ALLOC-0", "There are not enough resources to create the virtual machine"), //
    ALLOCATOR_ERROR(
        "ALLOC-1",
        "Cannot make the requested changes to the virtual machine because there are not enough free resources on the host machine. Please contact the Administrator."), NOT_VLAN_TAG_LEFT(
        "ALLOC-2", "There are not enough VLAN tags available"),

    CHECK_EDIT_NO_TARGET_MACHINE("EDIT-01",
        "This method requires the virtual machine to be deployed on a target hypervisor"),

    // VIRTUAL SYSTEM MONITOR

    MONITOR_PROBLEM("VSM-0", "An error occurred when monitoring the physical machine"), UNMONITOR_PROBLEM(
        "VSM-1", "An error occurred when shutting down the monitored physical machine"), SUBSCRIPTION_PROBLEM(
        "VSM-2", "An error occurred when subscribing the virtual machine"), UNSUBSCRIPTION_PROBLEM(
        "VSM-3", "An error occurred when unsubscribing the virtual machine"), REFRESH_STATE_PROBLEM(
        "VSM-4", "An error occurred when refreshing the virtual machine state"), INVALIDATE_STATE_PROBLEM(
        "VSM-5", "An error occurred when resetting the last known state of the virtual machine"), CHECK_AND_REFRESH_STATE_PROBLEM(
        "VSM-6", "An error occurred when checking and refreshing the virtual machine state"), VSM_UNAVAILABE(
        "VSM-7", "VSM service unavailable; check the URL of the service."), VSM_UNABLE_TO_DISABLE_MONITORING(
        "VSM-8", "Cannot disable virtual machine monitoring"), VSM_UNABLE_TO_ENABLE_MONITORING(
        "VSM-9", "Cannot enable virtual machine monitoring"), VM_DEF_SYNC_PROBLEM("VSM-10",
        "An error occurred when forcing the virtual machine definition sync"),

    // CLOUD PROVIDER PROXY
    CPP_UNAVAILABE("CPP-0", "CPP service unavailable; check the URL of the service."), //
    CPP_NOT_FOUND("CPP-1", "CPP remote service not found"),

    // LICENSE
    LICENSE_UNEXISTING("LICENSE-0", "The requested license does not exist"), LICENSE_INVALID(
        "LICENSE-1", "The license provided is not valid"), LICENSE_CIHPER_INIT_ERROR("LICENSE-2",
        "Could not initialize licensing ciphers"), LICENSE_CIHPER_KEY("LICENSE-3",
        "Could not read licensing cipher key"), LICENSE_OVERFLOW("LICENSE-4",
        "The maximum number of managed cores has been reached"), LICENSE_DUPLICATED("LICENSE-5",
        "The license is already being used"),

    // TIERS
    NON_EXISTENT_TIER("TIER-0", "The requested storage tier does not exist"), NULL_TIER("TIER-1",
        "Tier embedded in StoragePool cannot be null"), MISSING_TIER_LINK("TIER-2",
        "Missing link to storage tier"), TIER_PARAM_NOT_FOUND("TIER-3",
        "Missing storage tier parameter"), TIER_LINK_DATACENTER_PARAM_NOT_FOUND("TIER-4",
        "Datacenter parameter in storage tier link not found"), TIER_LINK_DATACENTER_DIFFERENT(
        "TIER-5",
        "The storage tier is not in the datacenter where you are trying to create the storage pool"), TIER_CONFLICT_DISABLING_TIER(
        "TIER-6", "Cannot disable a tier with associated storage pools"), TIER_DISABLED("TIER-7",
        "The requested storage tier is disabled"), TIER_LINK_VIRTUALDATACENTER_PARAM_NOT_FOUND(
        "TIER-8", "VirtualDatacenter parameter was not found in storage tier link"), TIER_LINK_VIRTUALDATACENTER_DIFFERENT(
        "TIER-9",
        "The virtual datacenter link of the storage tier does not match the virtual datacenter supplied"), TIER_INVALID_STORAGE_ALLOCATION_POLICY(
        "TIER-10", "The supplied storage allocation policy is not of a valid type"),

    // DEVICES
    NON_EXISTENT_DEVICE("DEVICE-0", "The requested device does not exist"), DEVICE_DUPLICATED(
        "DEVICE-1", "Duplicate storage device"),

    // STATISTICS
    NON_EXISTENT_STATS("STATS-0", "No statistical data found"), NON_EXISTENT_STATS_FOR_DATACENTER(
        "STATS-1", "No statistical data found for the requested datacenter"), NON_EXISTENT_STATS_FOR_DCLIMITS(
        "STATS-2", "No statistical data found for the requested enterprise in this datacenter"), NON_EXISTENT_STATS_FOR_ENTERPRISE(
        "STATS-3", "No statistical data found for the requested enterprise"), NODECOLLECTOR_ERROR(
        "NODECOLLECTOR-1", "Nodecollector has raised an error."),

    // QUERY PAGING STANDARD ERRORS
    QUERY_INVALID_PARAMETER("QUERY-0", "Invalid 'by' parameter"), QUERY_NETWORK_TYPE_INVALID_PARAMETER(
        "QUERY-1", "Invalid 'type' parameter. Only 'EXTERNAL', 'UNMANAGED' or 'PUBLIC' allowed"), QUERY_INVALID_RANGE(
        "QUERY-2", "Invalid range: 'startwith' parameter must be less than 'limit' parameter"),

    // VOLUME ERRORS
    VOLUME_GENERIC_ERROR("VOL-0", "Could not create the volume in the selected tier"), VOLUME_NOT_ENOUGH_RESOURCES(
        "VOL-1", "There are not enough resources in the selected tier to create the volume"), VOLUME_NAME_NOT_FOUND(
        "VOL-2", "The name of the volume is required"), NON_EXISTENT_VOLUME("VOL-3",
        "The volume does not exist"), VOLUME_CREATE_ERROR("VOL-4",
        "An unexpected error occurred while creating the volume"), VOLUME_MOUNTED_OR_RESERVED(
        "VOL-5", "The volume cannot be deleted because it is associated with a virtual machine"), VOLUME_SSM_DELETE_ERROR(
        "VOL-6", "Could not physically delete the volume from the target storage device"), VOLUME_DELETE_STATEFUL(
        "VOL-7",
        "The volume cannot be deleted because it is being used in a persistent template process"), VOLUME_DELETE_IN_VIRTUALAPPLIANCE(
        "VOL-8",
        "The persistent volume cannot be deleted because it is being used in a virtual appliance"), VOLUME_CONNECTION_NOT_FOUND(
        "VOL-9", "The connection field of the volume is required"), VOLUME_DECREASE_SIZE_LIMIT_ERROR(
        "VOL-10", "The size of the volume cannot be reduced"), VOLUME_NAME_LENGTH_ERROR("VOL-11",
        "The 'name' field of the volume cannot exceed 256 characters in length"), VOLUME_SIZE_INVALID(
        "VOL-13", "The size property must be a non-zero integer up to " + Rasd.LIMIT_MAX), VOLUME_IN_USE(
        "VOL-14", "The volume cannot be edited because it is being used in a virtual machine"), VOLUME_UPDATE(
        "VOL-15", "An unexpected error occurred and the volume could not be updated"), VOLUME_RESIZE_NOT_SUPPORTED(
        "VOL-17", "The volume does not support the resize operation"), SSM_UNREACHABLE("VOL-18",
        "Could not get the storage manager remote service"), VOLUME_GRANT_ACCESS_ERROR("VOL-19",
        "Could not add the initiator mappings"), NON_EXISTENT_VOLUME_MAPPING("VOL-20",
        "The requested initiator mapping does not exist"), VOLUME_NOT_ATTACHED("VOL-21",
        "The volume is not attached to the virtual machine"), VOLUME_ATTACH_INVALID_LINK("VOL-22",
        "Invalid link to the volume to attach"), VOLUME_ATTACH_INVALID_VDC_LINK("VOL-23",
        "Invalid virtual datacenter in the link to the volume to attach"), VOLUME_ALREADY_ATTACHED(
        "VOL-24", "The volume is already attached to a virtual machine"), VOLUME_TOO_MUCH_ATTACHMENTS(
        "VOL-25", "The maximum number of attached disks and volumes has been reached"), VOLUME_ATTACH_ERROR(
        "VOL-26",
        "An unexpected error occurred while attaching the volume. Please contact the Administrator"), VOLUME_ALREADY_DETACHED(
        "VOL-27", "The volume is already detached"), VOLUME_DETACH_ERROR("VOL-28",
        "An unexpected error occurred while detaching the volume. Please contact the Administrator"), VOLUME_RECONFIGURE_ERROR(
        "VOL-29", "An unexpected error occurred while reconfiguring storage"), VOLUME_WRONG_NEW_VIRTUALDATACENTER_DATACENTER(
        "VOL-39",
        "The volume can only be moved to another virtual datacenter in the same datacenter"), VOLUME_NOT_ENOUGH_RESOURCES_POOL(
        "VOL-40", "There are not enough resources in the selected pool to create the volume"), VOLUME_WRONG_NEW_VIRTUALDATACENTER_ENTERPRISE(
        "VOL-41",
        "The volume can only be moved to another virtual datacenter in the same enterprise"), VOLUME_PERSISTENT_IN_USE(
        "VOL-42",
        "The persistent volume cannot be moved because it is being used in a virtual machine"), VOLUME_GENERIC_ISCSI_INVALID_POOL(
        "VOL-43", "The pool must be iSCSI when creating generic iSCSI volumes"), VOLUME_GENERIC_ISCSI_INVALID_INTERNAL_UUID(
        "VOL-44", "There is already a volume with that internal name"), VOLUME_GENERIC_ISCSI_INTERNAL_UUID_NOT_FOUND(
        "VOL-45", "The internal name of the volume is required"), VOLUME_WITH_SNAPSHOTS("VOL-46",
        "The volume cannot be resized or removed from the virtual machine if it has snapshots"),

    // SSM
    SSM_GET_POOLS_ERROR("SSM-1", "Could not get the storage pools on the target storage device"), SSM_GET_POOL_ERROR(
        "SSM-2", "Could not get the given storage pool on the target storage device"), SSM_GET_VOLUMES_ERROR(
        "SSM-3", "Could not get the volumes in the given storage pool"), SSM_GET_VOLUME_ERROR(
        "SSM-4", "Could not get the given volume in the given storage pool"), SSM_CREATE_VOLUME_ERROR(
        "SSM-5", "Could not create the volume on the target storage device"), SSM_DELETE_VOLUME_ERROR(
        "SSM-6", "Could not delete the volume from the target storage device"), SSM_UPDATE_ERROR(
        "SSM-7", "Could not update the volume on the target storage device"), SSM_ADD_INITIATOR_ERROR(
        "SSM-8", "Could not add the given iSCSI initiator on the target storage device"), SSM_REMOVE_INITIATOR_ERROR(
        "SSM-9", "Could not remove the given iSCSI initiator from the target storage device"), SSM_GET_METADATA_ERROR(
        "SSM-10", "Could not get the metadata of the given storage type"),

    // RULES
    NON_EXISTENT_EER("RULE-1", "The requested restrict shared server rule does not exist"), NON_EXISTENT_FPR(
        "RULE-2", "The requested load balance rule does not exist"), NON_EXISTENT_MLR("RULE-3",
        "The requested load level rule does not exist"), ONE_FPR_REQUIRED("RULE-4",
        "At least one load balance rule is required"), ONE_LINK_REQUIRED("RULE-5",
        "Expected one link with the rel attribute with possible values (datacenter/rack/machine)"), INVALID_FPR(
        "RULE-6", "The load balance type indicated is null or invalid"), DUPLICATE_RULE(
        "RULE-7",
        "Cannot add more than one allocation rule for a set of infrastructure elements, i.e. datacenter and/or rack and/or server. Please check and remove rules with duplicate elements."),

    // HARD DISK
    HD_NON_EXISTENT_HARD_DISK("HD-1", "The requested hard disk does not exist"), HD_DISK_0_CAN_NOT_BE_DELETED(
        "HD-2", "Disk 0 is the system disk and cannot be deleted from the virtual machine"), HD_INVALID_DISK_SIZE(
        "HD-3", "Invalid disk size."), HD_CURRENTLY_ALLOCATED("HD-4",
        "Cannot perform this action because the hard disk is currently attached to a virtual machine"), HD_ATTACH_INVALID_LINK(
        "HD-5", "Invalid link to the hard disk to attach"), HD_ATTACH_INVALID_VDC_LINK("HD-6",
        "Invalid virtual datacenter in the link to the volume to attach"), HD_CREATION_NOT_UNAVAILABLE(
        "HD-7",
        "Cannot create a hard disk because this feature is not available for this hypervisor"), HD_DISK_DECREASE_SIZE_LIMIT_ERROR(
        "HD-8", "The size of the hard disk cannot be reduced"), HD_RESIZE_NOT_AVAILABLE("HD-9",
        "Cannot perform this action because hard disk resize is not available for this hypervisor"), HD_INVALID_ATTACHMENT_ORDER(
        "HD-10", "Invalid hard disk attachment sequence number"), HD_ATTACH_INVALID_VM("HD-11",
        "Cannot perform this action because the hard disk is not attached to a virtual machine"), HD_IN_USE(
        "HD-12", "The hard disk cannot be edited because it is being used by a virtual machine"),

    // Chef
    CHEF_ERROR_GETTING_RECIPES("CHEF-0",
        "Could not get the list of available recipes for the enterprise"), CHEF_ERROR_GETTING_ROLES(
        "CHEF-1", "Could not get the list of available Chef roles for the enterprise"), CHEF_UNEXPECTED_ERROR(
        "CHEF-2",
        "An unexpected error occurred while connecting to the Chef server. Please contact the Administrator."), CHEF_NODE_DOES_NOT_EXIST(
        "CHEF-3", "The node does not exist on the Chef Server. "
            + "If the virtual machine is bootstrapping, please wait until the process completes."), CHEF_ELEMENT_DOES_NOT_EXIST(
        "CHEF-4", "The given runlist element does not exist on the Chef Server"), CHEF_CANNOT_UPDATE_NODE(
        "CHEF-5", "The node could not be updated on the Chef Server. "
            + "Please contact the Administrator."), CHEF_CANNOT_CONNECT("CHEF-6",
        "Could not connect to the Chef server. Please contact the Administrator"), CHEF_INVALID_ENTERPRISE_DATA(
        "CHEF-7", "Could not connect to the Chef server with the given Admin data. "
            + "Please verify the credentials"), CHEF_INVALID_ENTERPRISE("CHEF-8",
        "The enterprise is not configured to use Chef"), CHEF_INVALID_VIRTUALMACHINE("CHEF-9",
        "The virtual machine cannot use Chef. "
            + "Please check that the template is Chef enabled and the Enterprise can use Chef"), CHEF_INVALID_VALIDATOR_KEY(
        "CHEF-10",
        "The validator certificate supplied is not a valid private key. Please verify the key format"), CHEF_INVALID_CLIENT_KEY(
        "CHEF-11",
        "The admin certificate supplied is not a valid private key. Please verify the key format"), CHEF_MALFORMED_URL(
        "CHEF-12", "The Chef server URL supplied is malformed"), CHEF_CLIENT_DOES_NOT_EXIST(
        "CHEF-13", "The validator client supplied does not exist"), CHEF_AUTHORIZATION(
        "CHEF-14",
        "Could not connect to the Chef server to perform the request. Please check that the client has the appropriate permissions to perform the task"),

    // Parsing links
    LINKS_INVALID_LINK("LNK-0", "Invalid link. Check documentation"), LINKS_ONLY_ACCEPTS_ONE_LINK(
        "LNK-1", "Invalid number of links: this resource only accepts a single link"), LINKS_VIRTUAL_MACHINE_TEMPLATE_NOT_FOUND(
        "LNK-2", "Virtual machine template link with rel 'virtualmachinetemplate' is mandatory"), LINKS_VIRTUAL_MACHINE_TEMPLATE_INVALID_URI(
        "LNK-3", "Virtual machine template link is invalid"), LINKS_PUBLIC_CLOUD_REGION_INVALID(
        "LNK-4", "Region link not found or invalid"),

    // CATEGORY
    NON_EXISTENT_CATEGORY("CATEGORY-1", "The requested category does not exist"), CATEGORY_DUPLICATED_NAME(
        "CATEGORY-2", "A category with this name already exists."), CATEGORY_NOT_ERASABLE(
        "CATEGORY-3", "This category cannot be deleted"), INVALID_CATEGORY_LINK("CATEGORY-4",
        "Invalid category identifier in the category link"), CATEGORY_CANNOT_BE_NULL("CATEGORY-5",
        "Category name cannot be null"), CATEGORY_CANNOT_MOVE_LOCAL("CATEGORY-6",
        "Cannot move a local category to another enterprise."), CATEGORY_NO_PRIVELIGES_TO_CREATE_GLOBAL(
        "CATEGORY-7", "Current user does not have enough privileges to create a global category."), CATEGORY_CANNOT_CHANGE_TO_LOCAL(
        "CATEGORY-8", "Cannot change a global category to a local category."), CATEGORY_NO_PRIVELIGES_TO_REMOVE(
        "CATEGORY-9", "Current user does not have enough privileges to remove this category."), CATEGORY_MUST_BE_GLOBAL(
        "CATEGORY-10", "Cannot share the VM template because it is in a local category"), CATEGORY_CANNOT_BE_LOCAL(
        "CATEGORY-11", "Cannot change the category to a local one if the VM template is shared"), CATEGORY_NO_PRIVELIGES_TO_MODIFY_LOCAL(
        "CATEGORY-12", "Current user does not have enough privileges to modify a local category."),
    // ICONS
    ICON_DUPLICATED_PATH("ICON-1", "Duplicate path for an icon"), NON_EXISTENT_ICON("ICON-2",
        "The requested icon does not exist"), NON_EXISENT_ICON_WITH_PATH("ICON-3",
        "No icon found with the requested path"), ICON_IN_USE_BY_VIRTUAL_IMAGES("ICON-4",
        "Cannot delete the icon because it is in use by a virtual machine template"), INVALID_ICON_LINK(
        "ICON-5", "Invalid icon identifier in the icon link"),

    // TASKS
    NON_EXISTENT_TASK("TASK-1", "The requested task does not exist"), TASK_INVALID_STATE("TASK-2",
        "The state is not in the appropiate state"),

    // PRICING TEMPLATE
    CURRENCY_PARAM_NOT_FOUND("PRICINGTEMPLATE-0", "Missing currency parameter"), ENT_PARAM_NOT_FOUND(
        "PRICINGTEMPLATE-1", "Missing enterprise parameter"), PRICING_TEMPLATE_DUPLICATED_NAME(
        "PRICINGTEMPLATE-2", "Duplicate name for pricing template"), NON_EXISTENT_PRICING_TEMPLATE(
        "PRICINGTEMPLATE-3", "The requested pricing template does not exist"), DELETE_ERROR_WITH_ENTERPRISE(
        "PRICINGTEMPLATE-4", "Cannot delete a pricing template with enterprise associated"), PRICING_TEMPLATE_MINIMUM_CHARGE_PERIOD(
        "PRICINGTEMPLATE-5", "The smallest charging period is DAY"), PRICING_TEMPLATE_EMPTY_NAME(
        "PRICINGTEMPLATE-6", "Pricing template name cannot be empty"), MISSING_CURRENCY_LINK(
        "PRICINGTEMPLATE-7", "Missing link to the currency"), CHARGING_PERIOD_VALUES(
        "PRICINGTEMPLATE-8", "Charging period values should be between 2 and 6"), MINIMUM_CHARGE_EMPTY(
        "PRICINGTEMPLATE-9", "Check minimum charge value is not null or wrong type"), MINIMUM_CHARGE_VALUES(
        "PRICINGTEMPLATE-10", "Minimum charge values should be between 0 and 6"),

    // CURRENCY
    NON_EXISTENT_CURRENCY("CURRENCY-0", "The requested currency does not exist"), ONE_CURRENCY_REQUIRED(
        "CURRENCY-1", "At least one currency is required"), CURRENCY_DUPLICATED_NAME("CURRENCY-2",
        "Duplicate name for currency"), CURRENCY_DELETE_ERROR("CURRENCY-3",
        "Cannot remove currency associated with a pricing model"), CURRENCY_NAME_NOT_FOUND(
        "CURRENCY-4", "Currency name is required"), CURRENCY_SYMBOL_NOT_FOUND("CURRENCY-5",
        "Currency symbol is required"), CURRENCY_NAME_LONG("CURRENCY-6",
        "Currency name: maximum length is 20 characters"), CURRENCY_SYMBOL_LONG("CURRENCY-7",
        "Currency symbol: maximum length is 10 characters"), CURRENCY_DIGIT_LONG("CURRENCY-8",
        "Currency digit: maximum value is 9"),

    // COST CODE
    NON_EXISTENT_COSTCODE("COSTCODE-0", "The requested cost code does not exist"), COSTCODE_PARAM_NOT_FOUND(
        "COSTCODE-1", "Missing cost code parameter"), COSTCODE_DUPLICATED_NAME("COSTCODE-2",
        "Duplicate name for cost code"), COSTCODE_NAME_NOT_FOUND("COSTCODE-3",
        "Cost code name is required"), COSTCODE_DESCRITPION_NOT_FOUND("COSTCODE-4",
        "Cost code description is required"), COSTCODE_NAME_LONG("COSTCODE-5",
        "Cost code name: maximum length is 20 characters"), COSTCODE_DESCRIPTION_LONG("COSTCODE-6",
        "Cost code description: maximum length is 100 characters"), COSTCODE_DELETE("COSTCODE-7",
        "Cost code cannot be deleted because it is assigned to one or more virtual machine templates"),

    // COST CODE- CURRENCY
    COSTCODE_CURRENCY_DUPLICATED("COSTCODE_CURRENCY-0", "Duplicate value by cost code and currency"), NON_EXISTENT_COSTCODE_CURRENCY(
        "COSTCODE_CURRENCY-1", "The requested cost code -currency does not exist"), NOT_ASSIGNED_COSTCODE_CURRENCY(
        "COSTCODE_CURRENCY-2", "The cost code -currency is not assigned to the cost code"), NOT_ASSIGNED_COSTCODE_CURRENCY_PRICE(
        "COSTCODE_CURRENCY-3", "Price is required"),

    // PRICING - COST CODE
    PRICING_COSTCODE_DUPLICATED("PRICING_COSTCODE-0",
        "Duplicate value by cost code and pricing template"), NON_EXISTENT_PRICING_COSTCODE(
        "PRICING_COSTCODE-1", "The requested cost code -pricing template does not exist"),

    // PRICING - TIER
    PRICING_TIER_DUPLICATED("PRICING_TIER-0", "Duplicate value by tier and pricing template"), NON_EXISTENT_PRICING_TIER(
        "PRICING_TIER-1", "The requested tier -pricing template does not exist"), PRICING_TIER_WRONG_RELATION(
        "PRICING_TIER-2", "The pricing tier is not related to the pricing model indicated"), PRICING_TIER_DATACENTER(
        "PRICING_TIER-3", "This tier is not related to the datacenter indicated"), NOT_ASSIGNED_PRICING_TIER_PRICE(
        "PRICING_TIER-4", "Price is required"), NOT_TIER_IN_PRICING_TIER("PRICING_TIER_5",
        "The tier indicated by the link is not related to this pricing tier"),

    // HYPERVISOR TYPE
    INVALID_HYPERVISOR_TYPE("HYPERVISOR_TYPE-0", "The requested hypervisor type is invalid"), HYPERVISOR_TYPE_LINK_NOT_FOUND(
        "HYPERVISOR_TYPE-1", "Missing hypervisor type link"), HYPERVISOR_TYPE_LINK_INVALID(
        "HYPERVISOR_TYPE-2", "Invalid hypervisor type link"),

    // REGION
    REGION_LINK_NOT_FOUND("REGION-0", "Missing region link"), REGION_LINK_INVALID("REGION-1",
        "Invalid region link"), REGION_NOT_FOUND("REGION-2",
        "Region not found in the provider plugin"),

    // DHCP_OPTION
    NON_EXISTENT_DHCP_OPTION("DHCP_OPTION-0", "The requested DHCP option does not exist"), DHCP_OPTION_PARAM_NOT_FOUND(
        "DHCP_OPTION-12", "Missing DHCP option parameter"),

    // MAIL
    ANY_RECEIVER("MAIL-0",
        "No recipient has been selected, please indicate owner and/or enterprise admin"), SENDER_WITHOUT_MAIL(
        "MAIL-1", "Sender does not have email address set"), ANY_RECIPIENT_FOUND("MAIL-2",
        "No recipient has been found"), BAD_INTERNET_ADDRESS_ERROR("MAIL-3",
        "Bad Internet address error"), BAD_JNDI_ERROR("MAIL-4", "Bad JNDI Error"), MESSAGE_REQUIRED(
        "MAIL-5", "Message body text is required"),

    // DVD
    NON_EXISTENT_DVD("DVD-1", "The requested DVD does not exist"), DVD_MISSING_IMAGE_LINK("DVD-2",
        "Virtual machine template link with rel 'image' is mandatory "),

    // REDIS
    REDIS_CONNECTION_FAILED("REDIS-0", "Failed connection to Redis"),

    // RABBITMQ
    RABBITMQ_CONNECTION_FAILED("RABBITMQ-0", "Failed connection to RabbitMQ"),

    // SCOPE
    SCOPE_DUPLICATED_NAME("SCOPE-0", "Duplicate name for scope"), NON_EXISTENT_SCOPE("SCOPE-1",
        "The requested scope does not exist"), DEFAULT_SCOPE_DELETE("SCOPE-2",
        "Cannot delete the default scope"), DEFAULT_SCOPE_EDIT("SCOPE-3",
        "Cannot modify the default scope"), SCOPE_ROLE_ENTERPRISE("SCOPE-4",
        "The role supplied in user creation does not have this enterprise visible in its scope"), SCOPE_WITH_ROLE(
        "SCOPE-5", "Cannot remove the scope because there are roles associated with it"), SCOPE_USERS_OUT_OF_ENTERPRISES(
        "SCOPE-6",
        "Cannot associate role with requested scope, because there are users with enterprises not included in this scope."), SCOPE_INVALID_LINK(
        "SCOPE-7", "Scope invalid link"), SCOPE_CANNOT_MODIFY(
        "SCOPE-8",
        "Cannot modify the scope because the enterprise to be removed from scope is the enterprise of a user with this scope assigned"),

    // PERSISTETNT
    PERSISTENT_TEMPLATE_IS_PERSITENT("PERSISTENT-1",
        "The virtual machine template is already persistent"), PERSISTENT_RAW_CONVERSION_NOT_EXISTS(
        "PERSISTENT-2",
        "A RAW format conversion does not exist for the virtual machine template but is required to make it persistent"), PERSISTENT_NEW_NAME_NOT_FOUND(
        "PERSISTENT-3", "No name given for the new persistent template"), PERSISTENT_NO_VOLUME_TIER_LINK_FOUND(
        "PERSISTENT-4",
        "No tier or volume for storing the persistent virtual machine template was found in the request but one of these is required"), PERSISTENT_NO_POOLS_IN_TIER(
        "PERSISTENT-5", "There are no pools available in at least one of the selected tiers"), PERSISTENT_CAN_NOT_CREATE_VOLUME(
        "PERSISTENT-6",
        "An error occurred while creating the volume. Please contact your administrator"), PERSISTENT_ALREADY_ATTACHED(
        "PERSISTENT-7",
        "Another virtual machine is already using the persistent virtual machine template."), PERSISTENT_NO_TEMPLATE_LINK_FOUND(
        "PERSISTENT-8", "No virtual machine template found to persist"), PERSISTENT_INVALID_TIER_LINK(
        "PERSISTENT-9", "Invalid tier link"), PERSISTENT_INVALID_VOLUME_LINK("PERSISTENT-10",
        "Invalid volume link"), PERSISTENT_INVALID_VDC_LINK("PERSISTENT-11",
        "Invalid virtual datacenter link"), PERSISTENT_NO_VDC_LINK_FOUND("PERSISTENT-12",
        "No virtual datacenter found for the persistent virtual machine"), PERSISTENT_CONSTRAINT_LENGTH_NAME_MIN(
        "CONSTR-LENGTH-NAME-MIN", "The length of the NAME parameter must be greater than "
            + VirtualMachineTemplate.NAME_LENGTH_MIN + " characters"), PERSISTENT_CONSTRAINT_LENGTH_NAME_MAX(
        "CONSTR-LENGTH-NAME-MAX", "The length of the NAME parameter must be less than "
            + VirtualMachineTemplate.NAME_LENGTH_MAX + " characters"), PERSISTENT_CONSTRAINT_LENGTH_VOL_NAME_MIN(
        "CONSTR-LENGTH-VOL-NAME-MIN", "The length of the NAME parameter must be greater than "
            + Rasd.ELEMENT_NAME_LENGTH_MIN + " characters"), PERSISTENT_CONSTRAINT_LENGTH_VOL_NAME_MAX(
        "CONSTR-LENGTH-VOL-NAME-MAX", "The length of the NAME parameter must be less than "
            + Rasd.ELEMENT_NAME_LENGTH_MAX + " characters"), PERSISTENT_CAN_NOT_ADD_INITIATOR(
        "PERSISTENT-13", "Unable to create the initiator mapping for the volume."), PERSISTENT_VDC_WRONG_ENTERPRISE(
        "PERSISTENT-14", "The virtual datacenter does not belong to the correct enterprise"),

    // EVENTS
    NON_EXISTENT_EVENT("EVENT-0", "This event does not exist"),

    NST_MUST_BE_INFORMED("NST-0", "The name of the network service type must be supplied"), NST_DUPLICATED_NAME(
        "NST-1", "The name of the network service type already exists"), NON_EXISTENT_NST("NST-2",
        "The requested network service type does not exist"), NST_SACRED_DEFAULT_NST("NST-3",
        "You can edit the current default network service type for a datacenter but you cannot delete it."), NST_HAVE_AT_LEAST_ONE(
        "NST-4",
        "A datacenter requires at least one network service type and there is only one left."), NST_INVALID_LINK(
        "NST-5", "Invalid network service type identifier in the enterprise link"), NST_LINK_NOT_FOUND(
        "NST-6", "Missing network service type link"), NST_CANNOT_CHANGED("NST-7",
        "Cannot change network service type because there are some virtual machines using IPs in this VLAN"), NST_ATTACHED_TO_PHYSICAL_MACHINE(
        "NST-8",
        "Network service type cannot be deleted because it is assigned to physical machine(s)"), NST_ATTACHED_TO_VLAN(
        "NST-9", "Network service type cannot be deleted because it is assigned to VLAN(s)"), NST_LINK_WRONG_DATACENTER(
        "NST-10", "Invalid datacenter identifier in the network service type link"), NST_INVALID_NST_DISTRIBUTED_NI(
        "NST-11",
        "Invalid network service type for '%s'. This distributed network interface must have an associated network service type. Conflict with machine '%s (%s)' "), NST_INVALID_NST_CONFLICT(
        "NST-12",
        "Conflict error trying to assign network service '%s' to network interface '%s'. Conflict with machine '%s (%s)'",
        DATACENTER.CREATE.addErrorkeys(new KEYS[] {KEYS.INITIATORIQN})),
    // WORKFLOW
    WORKFLOW_REQUEST_ERROR("WKF-01", "Request to workflow endpoint failed"),
    // 02 and 03 Never outuputted to the user, only for tracing
    WORKFLOW_CANCEL_ERROR("WKF-02", "Cancelation of a workflow task failed"), WORKFLOW_CONTINUE_ERROR(
        "WKF-03", "Continuation of a workflow task failed"),

    // HYPERVISOR PLUGIN ERROR

    HYPERVISOR_CONNECTION("HYP-01", "Unable to connect; invalid hypervisor location (IP or port)"), @Deprecated
    // controled in API
    IDE_CONTROLLER_FULL("ED-1", "Cannot attach more than 4 disks to IDE controller"), RECONFIG(
        "VM-02", "Error reconfiguring virtual machine"),

    // BACKUP
    NON_EXISTENT_BACKUP_TEMPLATE("BC-1", "The requested backup template does not exist"), BACKUP_DC_ALREADY_EXISTS(
        "BC-2", "A backup with the requested template already exists"), NON_EXISTENT_BACKUP_DATACENTER(
        "BC-3", "The requested backup datacenter does not exist"), NON_EXISTENT_ATTRIBUTE("BC-4",
        "The requested backup attribute does not exist"), NON_VALID_TYPE(
        "BC-5",
        "Some backup type values are wrong. Check that all type values are correctly spelled and written in lower-case letters"), NOT_ENABLED(
        "BC-6", "Some of the requested options are not enabled"), NON_VALID_DAY("BC-7",
        "Some backup week days are wrong"), NON_VALID_PERIOD("BC-8",
        "Some of the requested periods are wrong"), FILESYSTEM_PATHS("BC-9",
        "Paths can only be requested for filesystem backup type"), DISKS("BC-10",
        "Disks can only be requested for complete or snapshot backup type"), TIME_NOT_INDICATED(
        "BC11", "Time value not supplied"), INVALID_DATE_FORMAT("BC-12",
        "Invalid time. Time format should be " + VirtualMachineMetadataService.TIME_FORMAT), INVALID_DATE_FORMAT_DEFINED(
        "BC-13", "Invalid date. Date format should be " + VirtualMachineMetadataService.DATE_FORMAT), BACKUP_NOT_FOUND(
        "BC-14", "Missing backup values"), RESULTS_STATUS("BC-15",
        "Valid status values are: done, progress, failed"), INVALID_HOURLY("BC-16",
        "Accepted values for hours backup are 0 to 24. E.g., every 5 hours"), INVALID_DISKS(
        "BC-17", "Invalid values for disks. Provide a list of disks"), INVALID_PATHS("BC-18",
        "Invalid values for paths. Provide a list of paths"), WEEKLY_WITHOUT_DAY("BC-19",
        "Weekly planned backup requires a day to be selected"),

    // PUBLIC CLOUD
    NON_EXISTENT_CREDENTIALS("PUBLIC-1", "The requested credentials do not exist"), PUBLIC_CONNECTION_PROBLEM(
        "PUBLIC-2", "Unable to establish connection to the endpoint of the public cloud region"), CREDENTIALS_ALREADY_ASSIGNED(
        "PUBLIC-3", "The requested provider already has credentials assigned."), PUBLIC_PROVIDER_NOT_SUPPORTED(
        "PUBLIC-4", "The requested provider is not supported."), PUBLIC_OPERATION_NOT_SUPPORTED(
        "PUBLIC-5", "The requested operation is not supported in a public cloud region"), NON_PUBLIC_OPERATION_NOT_SUPPORTED(
        "PUBLIC-6", "The requested operation is not supported in a datacenter"), CREDENTIALS_CANT_MODIFY_PROVIDER(
        "PUBLIC-7", "The provider field of public cloud credentials cannot be modified"), MISSING_PUBLIC_CLOUD_REGION_LINK(
        "PUBLIC-8", "Missing public cloud region link"), PUBLIC_CLOUD_NO_SUCH_PROVIDER("PUBLIC-9",
        "Plugin not loaded or not a cloud provider"), CREDENTIALS_NON_EXISTENT_CANNOT_DO_ACTION(
        "PUBLIC-10", "The enterprise does not have credentials for this provider"), CREDENTIALS_CHECK_REQUIRED_ACCES_KEY(
        "PUBLIC-11", "The access ID and secret key are required to validate the credentials"), CREDENTIALS_CHECK_INVALID(
        "PUBLIC-12", "The given credentials are not valid for this region"), CREDENTIAL_CAN_NOT_BE_CHECKED(
        "PUBLIC-13",
        "The given credentials could not be validated. At least one public cloud region of the same provider type must be allowed for this enterprise in order to verify the credentials"), CREDENTIAL_IN_USE(
        "PUBLIC-14", "The supplied public cloud credentials are already in use."),

    // HARDWARE PROFILE
    HARDWARE_PROFILE_DUPLICATED_NAME("PROFILE-0", "Duplicate name for hardware profile"), NON_EXISTENT_HARDWARE_PROFILE(
        "PROFILE-1", "The requested hardware profile does not exist"), HARDWARE_PROFILE_CANNOT_CREATE_IN_PUBLIC(
        "PROFILE-2",
        "Cannot create the hardware profile because the requested datacenter is a public datacenter"), INVALID_HARDWARE_PROFILE_LINK(
        "PROFILE-3", "Invalid link to the hardware profile; cannot get profile values"), HARDWARE_PROFILE_LINK_NOT_FOUND(
        "PROFILE-5", "Missing hardware profile link"),

    // VERSION
    BAD_VERSION("VERSION-1", "Invalid version format"), NON_EXISTENT_LOGO("VERSION-2",
        "No logo exists for the requested version"),

    // PUBLIC CLOUD REGION
    PUBLIC_CLOUD_REGION_MISSING_ENT_LINK("PUBLICREGION-1", "The enterprise link is required"), PUBLIC_CLOUD_REGION_NAME_REQUIRED(
        "PUBLICREGION-2", "The name is required"), PUBLIC_CLOUD_REGION_REGION_DUPLICATED(
        "PUBLICREGION-5", "The region is already used by another public cloud region"), PUBLIC_CLOUD_REGION_NAME_DUPLICATED(
        "PUBLICREGION-6", "The name is already used by another public cloud region"), PUBLIC_CLOUD_REGION_INVALID_REMOTE_SERVICES(
        "PUBLICREGION-7", String.format(
            "The remote services for a public cloud region must be: the %s, the %s and the %s",
            NODE_COLLECTOR.getName(), VIRTUAL_FACTORY.getName(), VIRTUAL_SYSTEM_MONITOR.getName())), NON_EXISTENT_PUBLIC_CLOUD_REGION(
        "PUBLICREGION-8", "The requested public cloud region does not exist"), PUBLIC_CLOUD_REGION_REGION_INVALID(
        "PUBLICREGION-9",
        "The requested public cloud region is invalid for the given public cloud provider"), PUBLIC_CLOUD_REGION_ENDPOINT_UNAVAILABLE(
        "PUBLICREGION-10", "Cannot obtain the endpoint of the given provider type and region."), PUBLIC_CLOUD_REGION_DELETE_VIRTUAL_DATACENTERS(
        "PUBLICREGION-11",
        "Cannot delete a public cloud region that is being used by some virtual datacenters"), PUBLIC_CLOUD_REGION_REGION_UNMODIFIABLE(
        "PUBLICREGION-12", "The region of a public cloud region cannot be modified"), PUBLIC_CLOUD_REGION_TYPE_UNMODIFIABLE(
        "PUBLICREGION-13", "The provider type of a public cloud region cannot be modified"), PUBLIC_CLOUD_REGION_STORAGE_LIMIT_UNSUPPORTED(
        "PUBLICREGION-14", "The requested public cloud region does not support storage limits"), PUBLIC_CLOUD_REGION_REPOSITORY_LIMIT_UNSUPPORTED(
        "PUBLICREGION-15", "The requested public cloud region does not support repository limits"),

    // LOCATIONS
    LOCATION_LINK_NOT_FOUND("LOCATION-1", "The location link is required"), LOCATION_LINK_INVALID(
        "LOCATION-2", "The location link is not valid"), LOCATION_TYPE_UNCOMPATIBLE("LOCATION-3",
        "The given provider type is not compatible with the given location"), LOCATION_NOT_FOUND(
        "LOCATION-4", "The location does not exist"), LOCATION_INCOMPATIBLE_VDC("LOCATION-5",
        "The given virtual datacenter is not comptaible with the given location"),

    // AVAILABILITY ZONES
    AVAILABILITY_ZONE_NOT_FOUND("AVZONE-1", "The availability zone does not exist"), AVAILABILITY_ZONE_LINK_INVALID(
        "AVZONE-2", "The availability zone link is not valid"), AVAILABILITY_ZONE_LINK_NOT_FOUND(
        "AVZONE-3", "The availability zone link is required"),

    // FLOATING IPS
    FLOATING_IP_NOT_FOUND("FLOATINGIP-1", "The floating IP does not exist"), FLOATING_IP_ATTACHED(
        "FLOATINGIP-2",
        "The floating IP is currently used by a virtual machine. It cannot be released"), FLOATING_IP_CANNOT_CREATE(
        "FLOATINGIP-3", "Cannot create a floating IP in the given location"),

    // CLOUD NODES
    NON_EXISTENT_PUBLIC_CLOUD_NODE_UID("CLOUDNODE-1", "The requested cloud node UID does not exist"),

    // FIREWALL
    FIREWALL_POLICY_NAME_REQUIRED("FIREWALLPOLICY-1", "The name is required"), FIREWALL_POLICY_DESCRIPTION_REQUIRED(
        "FIREWALLPOLICY-2", "The description is required"), FIREWALL_POLICY_NOT_FOUND(
        "FIREWALLPOLICY-5", "The requested firewall policy does not exist"), FIREWALL_POLICY_HAS_MEMBERS(
        "FIREWALLPOLICY-6",
        "The firewall policy is being used by virtual machines and/or load balancers and cannot be modified"),

    FIREWALL_RULE_PROTOCOL_REQUIRED("FIREWALLRULE-1", "The protocol is required"), FIREWALL_RULE_FROM_PORT_REQUIRED(
        "FIREWALLRULE-2", "The from port field is required"), FIREWALL_RULE_TO_PORT_REQUIRED(
        "FIREWALLRULE-3", "The to port field is required"), FIREWALL_RULE_TARGETS_OR_SOURCES_REQUIRED(
        "FIREWALLRULE-4",
        "The targets list is required for an egress rule or the sources list for an ingress rule"), FIREWALL_RULE_TARGETS_AND_SOURCES_INVALID(
        "FIREWALLRULE-5",
        "Firewall rule can be egress or ingress but not both. Complete either targets or sources only"),

    // METRICS
    NULL_OR_EMPTY_METRIC_NAME("METRIC-1", "The metric name is required"), //
    UNKNOWN_METRIC("METRIC-2", "Unknown metric"), //
    ILLEGAL_METRIC_TIME_RANGE("METRIC-3", "Illegal time range for a query"), //
    TIME_SERIES_DATABASE_UNAVAILABLE("METRIC-4", "Could not connect to the time series database"), //
    ABSOLUTE_TIME_INVALID_FORMAT("METRIC-5",
        "The supplied absolute time does not match the pattern yyyyMMddHHmmz"), //
    RELATIVE_TIME_INVALID_FORMAT("METRIC-6", "The supplied relative time contains years or months"), //
    RELATIVE_TIME_OUT_OF_RANGE("METRIC-7", "The supplied relative time is out of range"), //
    PROVIDED_RELATIVE_AND_ABSOLUTE_START_TIME("METRIC-8",
        "Both relative and absolute start time cannot be provided"), //
    PROVIDED_RELATIVE_START_TIME_AND_END_TIME("METRIC-9",
        "Relative start time cannot be provided with an end time"), //
    RELATIVE_START_TIME_GREATER_THAN_ONE_WEEK("METRIC-10",
        "The relative start time must be shorter than 1 week"), //
    GRANULARITY_SMALLER_THAN_ONE_MINUTE("METRIC-11", "The minimum granularity is 60 seconds"), //
    VM_MONITORING_NOT_ENABLED("METRIC-12",
        "The requested virtual machine is not enabled for monitoring"), //
    IMPORTED_VM_CAN_NOT_BE_MONITORED("METRIC-13",
        "Cannot collect metrics for imported virtual machines"), //

    // LOAD BALANCER

    LOAD_BALANCER_NOT_FOUND("LOADBALANCER-1", "The requested load balancer does not exist"), ROUTING_RULE_NOT_FOUND(
        "LOADBALANCER-2", "The requested routing rule does not exist"), ROUTING_RULE_CANNOT_BE_DELETED(
        "LOADBALANCER-3",
        "This is the only routing rule of the load balancer and it cannot be deleted. To remove it, delete the load balancer."), ROUTING_RULE_REPEATED(
        "LOADBALANCER-4",
        "A routing rule with the same port in and port out already exists for the load balancer"), ROUTING_RULE_CANNOT_CHANGE_LOADBALANCER(
        "LOADBALANCER-5", "Routing rule belongs to a load balancer that cannot be modified"), SSL_CERTIFICATE_MISSING_FIELDS(
        "LOADBALANCER-6", "Not all required fields are present for the embedded SSL certificate"), SSL_CERTIFICATE_MUST_PROVIDE_LINK_OR_EMBEDDED(
        "LOADBALANCER-7",
        "A link to the SSL certificate or the embedded form must be used, but not both at the same time."), LOAD_BALANCER_ADDRESS_NOT_FOUND(
        "LOADBALANCER-8", "The requested load balancer address does not exist"), HEALTH_CHECK_PATH_PROTOCOL_ALREADY_SET(
        "LOADBALANCER-9",
        "There is already one health check with the same path and protocol in the load balancer"), HEALTH_CHECK_NOT_FOUND(
        "LOADBALANCER-10", "The requested health check does not exist in the load balancer"), LOAD_BALANCER_VIRTUAL_DATACENTER_VLAN_CONFLICT(
        "LOADBALANCER-11", "Selected private networks are not in the same virtual datacenter"), LOAD_BALANCER_VIRTUAL_DATACENTER_FIREWALL_CONFLICT(
        "LOADBALANCER-12", "Selected firewalls are not in the same virtual datacenter"), LOAD_BALANCER_SSLCERTIFICATE_REQUIRE_CREATION(
        "LOADBALANCER-13",
        "Certificate cannot be stored in the provider. Ensure the load balancer is properly created"), LOAD_BALANCER_ADDRESS_REQUIRE_CREATION(
        "LOADBALANCER-14",
        "Address cannot be selected in the provider. Ensure the load balancer is properly created"), LOAD_BALANCER_ATTACH_VM_REQUIRE_CREATION(
        "LOADBALANCER-15",
        "Virtual machines cannot be attached to load balancer in the provider. Ensure the load balancer is properly created"), LOAD_BALANCER_NON_COMPATIBLE_VMS(
        "LOADBALANCER5-16", "Given virtual machines are not compatible with the load balancer"), LOAD_BALANCER_VMS_UNEXPECTED_ERROR(
        "LOADBALANCER5-17",
        "An unexpected error occurred while assigning virtual machines to load balancer"), SSL_CERTIFICATE_NOT_FOUND(
        "LOADBALANCER-18", "The requested SSL certificate does not exist"), LOAD_BALANCER_IMPORT_VLAN_NOT_FOUND(
        "LOADBALANCER-19", "VLAN not found during load balancer import"), LOAD_BALANCER_IMPORT_VDC_VLAN_NOT_FOUND(
        "LOADBALANCER-20",
        "Load balancer has VLAN networks but cannot select the virtual datacenter"), LOAD_BALANCER_IMPORT_FIREWALL_NOT_FOUND(
        "LOADBALANCER-21",
        "Firewall not found while importing load balancer; try to synchronize firewalls before load balancers"), LOAD_BALANCER_FLAT_PUT(
        "LOADBALANCER-22",
        "Cannot embed RoutingRules, HealthChecks or LoadBalancerAddresses during modify, use subresources instead"), LOAD_BALANCER_FIREWALL_MUST_EXIST(
        "LOADBALANCER-23",
        "Create firewall policies in the provider before assigning them to the load balancer"), LOAD_BALANCER_NO_RULES(
        "LOADBALANCER-24", "Load balancer requires at least one routing rule"), LOAD_BALANCER_VLAN_MUST_EXIST(
        "LOADBALANCER-25",
        "Create VLAN networks (or elastic IPs) in the provider before assigning them to the load balancer");

    /**
     * Internal error code
     */
    String code;

    /**
     * Description message
     */
    String message;

    /**
     * Required key args to build the message
     */
    Action< ? >[] keys;

    private APIError(final String code, final String message)
    {
        this.code = code;
        this.message = message;
    }

    private APIError(final String code, final String message, final Action< ? >... keys)
    {
        this.code = code;
        this.message = message;
        this.keys = keys;
    }

    @Override
    public String getCode()
    {
        return String.valueOf(this.code);
    }

    @Override
    public String getMessage()
    {
        return this.message;
    }

    private static String fromStatus(final int statusCode)
    {
        // Use the wink class because the JAX-RS Status enumeration does not contain all standard
        // status codes
        HttpStatus status = HttpStatus.valueOf(statusCode);
        return status.toString().toUpperCase().replaceAll("\\s", "-");
    }

    public static APIError fromVirtualMachineDescriptionBuilderError(
        final VirtualMachineDescriptionBuilderError error)
    {
        switch (error)
        {
            case IDE_FULL:
                return VIRTUAL_MACHINE_IDE_CONTROLLER_FULL;
            case SEQUENCE_REPETITION:
                return VIRTUAL_MACHINE_DISK_SEQUENCE_REPETITION;
            case NO_PRIMARY_DISK:
            case NO_IDENTIFIED:
            case NO_HARDWARE:
                return VIRTUAL_MACHINE_DEFINITION_INVALID;
            default:
                return GENERIC_OPERATION_ERROR;
        }
    }

    public static AbiquoError fromCommonError(final CommonError error)
    {
        return new AbiquoError()
        {

            @Override
            public String getMessage()
            {
                return error.getMessage();
            }

            @Override
            public String getCode()
            {
                return error.getCode();
            }
        };
    }

    public static AbiquoError fromHypervisorPluginError(final HypervisorPluginError error)
    {
        return new AbiquoError()
        {

            @Override
            public String getMessage()
            {
                return error.getMessage();
            }

            @Override
            public String getCode()
            {
                return error.getCode();
            }
        };
    }

    public static AbiquoError fromCloudProviderError(final HypervisorPluginError error)
    {
        return new AbiquoError()
        {

            @Override
            public String getMessage()
            {
                return error.getMessage();
            }

            @Override
            public String getCode()
            {
                return error.getCode();
            }
        };
    }

    public static final Comparator<AbiquoError> ORDER_BY_CODE = new Comparator<AbiquoError>()
    {
        @Override
        public int compare(final AbiquoError o1, final AbiquoError o2)
        {
            return String.CASE_INSENSITIVE_ORDER.compare(o1.getCode(), o2.getCode());
        }
    };

    public static final Function<AbiquoError, CommonError> TO_COMMON_ERROR =
        new Function<AbiquoError, CommonError>()
        {
            @Override
            public CommonError apply(final AbiquoError input)
            {
                return new CommonError(input.getCode(), input.getMessage());
            };
        };

    public static final Function<CommonError, AbiquoError> FROM_COMMON_ERROR =
        new Function<CommonError, AbiquoError>()
        {

            @Override
            public AbiquoError apply(final CommonError input)
            {
                return fromCommonError(input);
            }
        };

    public static void main(final String[] args)
    {
        APIError[] errors = APIError.values();
        Arrays.sort(errors, ORDER_BY_CODE);

        System.out.println("\n ************ Wiki errors ************** \n");

        // Outputs all errors in wiki table format
        for (APIError error : errors)
        {
            System.out.println(String.format("| %s | %s | %s |", error.code, error.message,
                error.name()));
        }

        System.out.println("\n ************ Flex client labels ************** \n");

        // Outputs all errors for the Chef client
        for (APIError error : errors)
        {
            System.out.println(String.format("%s=%s", error.code, error.message));
        }
    }

}
