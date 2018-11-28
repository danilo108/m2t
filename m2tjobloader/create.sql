create table client (id bigint not null, address varchar(255), alarm varchar(255), code varchar(255), client_type varchar(255), default_zone varchar(255), gate_code varchar(255), lock varchar(255), name varchar(255), phone varchar(255), primary key (id)) engine=MyISAM
create table container (id bigint not null, arrival datetime, number varchar(255), fullurl varchar(255), original_file_name varchar(255), report_fullurl varchar(255), report_sheet_id varchar(255), sheet_id varchar(255), primary key (id)) engine=MyISAM
create table hibernate_sequence (next_val bigint) engine=MyISAM
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
create table job (id bigint not null, arrived_on datetime, container varchar(255), delivery_to_code varchar(255), delivery_address varchar(255), job_client varchar(255), job_code varchar(255), notes varchar(255), original_delivery_address varchar(255), size varchar(255), size_sqm float, tot_blinds integer, tot_boxes integer, tot_frames integer, tot_hardware integer, tot_panels integer, deliver_to_id bigint, original_client bigint not null, job_id bigint, primary key (id)) engine=MyISAM
alter table client add constraint UK_mjxc7a55xsadk5s0yqxq956tn unique (code)
alter table container add constraint UK_dckuhkeriawy0s6d3soq7sggo unique (number)
alter table job add constraint FKcgmvt0yktt4ur5huvx45vkdk5 foreign key (deliver_to_id) references client (id)
alter table job add constraint FKof8u4rjaxdwn77djgus2aymmy foreign key (original_client) references client (id)
alter table job add constraint FK1l2yycdi347xr0gxuxqtaxhdl foreign key (job_id) references container (id)
create table client (id bigint not null, address varchar(255), alarm varchar(255), code varchar(255), client_type varchar(255), default_zone varchar(255), gate_code varchar(255), lock varchar(255), name varchar(255), phone varchar(255), primary key (id)) engine=MyISAM
create table container (id bigint not null, arrival datetime, number varchar(255), fullurl varchar(255), original_file_name varchar(255), report_fullurl varchar(255), report_sheet_id varchar(255), sheet_id varchar(255), primary key (id)) engine=MyISAM
create table hibernate_sequence (next_val bigint) engine=MyISAM
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
create table job (id bigint not null, arrived_on datetime, container varchar(255), delivery_to_code varchar(255), delivery_address varchar(255), job_client varchar(255), job_code varchar(255), notes varchar(255), original_delivery_address varchar(255), size varchar(255), size_sqm float, tot_blinds integer, tot_boxes integer, tot_frames integer, tot_hardware integer, tot_panels integer, deliver_to_id bigint, original_client bigint not null, job_id bigint, primary key (id)) engine=MyISAM
alter table client add constraint UK_mjxc7a55xsadk5s0yqxq956tn unique (code)
alter table container add constraint UK_dckuhkeriawy0s6d3soq7sggo unique (number)
alter table job add constraint FKcgmvt0yktt4ur5huvx45vkdk5 foreign key (deliver_to_id) references client (id)
alter table job add constraint FKof8u4rjaxdwn77djgus2aymmy foreign key (original_client) references client (id)
alter table job add constraint FK1l2yycdi347xr0gxuxqtaxhdl foreign key (job_id) references container (id)
create table client (id bigint not null, address varchar(255), alarm varchar(255), code varchar(255), client_type varchar(255), default_zone varchar(255), gate_code varchar(255), lock varchar(255), name varchar(255), phone varchar(255), primary key (id)) engine=MyISAM
create table container (id bigint not null, arrival datetime, number varchar(255), fullurl varchar(255), original_file_name varchar(255), report_fullurl varchar(255), report_sheet_id varchar(255), sheet_id varchar(255), primary key (id)) engine=MyISAM
create table hibernate_sequence (next_val bigint) engine=MyISAM
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
create table job (id bigint not null, arrived_on datetime, container varchar(255), delivery_to_code varchar(255), delivery_address varchar(255), job_client varchar(255), job_code varchar(255), notes varchar(255), original_delivery_address varchar(255), size varchar(255), size_sqm float, tot_blinds integer, tot_boxes integer, tot_frames integer, tot_hardware integer, tot_panels integer, deliver_to_id bigint, original_client bigint not null, job_id bigint, primary key (id)) engine=MyISAM
alter table client add constraint UK_mjxc7a55xsadk5s0yqxq956tn unique (code)
alter table container add constraint UK_dckuhkeriawy0s6d3soq7sggo unique (number)
alter table job add constraint FKcgmvt0yktt4ur5huvx45vkdk5 foreign key (deliver_to_id) references client (id)
alter table job add constraint FKof8u4rjaxdwn77djgus2aymmy foreign key (original_client) references client (id)
alter table job add constraint FK1l2yycdi347xr0gxuxqtaxhdl foreign key (job_id) references container (id)
