INSERT into inventory_item (item_name, item_sku, reorder_threshold, item_unit, is_active, created_at)
VALUES
('Widget A', 'WID-A-001', 10, 'each', true, '2024-01-10 10:00:00'),
('Widget B', 'WID-B-002', 15, 'each', true, '2024-01-11 11:00:00'),
('Gadget C', 'GAD-C-003', 5, 'each', true, '2024-01-12 12:00:00'),
('Gadget D', 'GAD-D-004', 20, 'each', true, '2024-01-13 13:00:00'),
('Tool E', 'TOL-E-005', 8, 'each', true, '2024-01-14 14:00:00'),
('Tool F', 'TOL-F-006', 12, 'each', true, '2024-01-15 09:00:00'),
('Tool G', 'TOL-G-007', 3, 'each', true, '2024-01-16 10:15:00'),
('Widget H', 'WID-H-008', 25, 'each', true, '2024-01-17 11:30:00'),
('Widget I', 'WID-I-009', 7, 'each', true, '2024-01-18 12:45:00'),
('Gadget J', 'GAD-J-010', 18, 'each', true, '2024-01-19 14:00:00'),
('Gadget K', 'GAD-K-011', 2, 'each', true, '2024-01-20 15:15:00'),
('Part L', 'PRT-L-012', 30, 'each', true, '2024-01-21 16:30:00'),
('Part M', 'PRT-M-013', 6, 'each', true, '2024-01-22 08:20:00'),
('Component N', 'CMP-N-014', 14, 'each', true, '2024-01-23 09:40:00'),
('Component O', 'CMP-O-015', 9, 'each', true, '2024-01-24 10:55:00'),
('Supply P', 'SUP-P-016', 22, 'each', true, '2024-01-25 12:10:00'),
('Supply Q', 'SUP-Q-017', 4, 'each', true, '2024-01-26 13:25:00'),
('Device R', 'DEV-R-018', 17, 'each', true, '2024-01-27 14:40:00'),
('Device S', 'DEV-S-019', 11, 'each', true, '2024-01-28 15:55:00'),
('Accessory T', 'ACC-T-020', 19, 'each', true, '2024-01-29 17:10:00'),
('Old Widget U', 'WID-U-021', 5, 'each', false, '2024-02-01 09:00:00'),
('Legacy Gadget V', 'GAD-V-022', 10, 'each', false, '2024-02-02 10:30:00'),
('Retired Tool W', 'TOL-W-023', 2, 'each', false, '2024-02-03 11:15:00'),
('Discontinued Part X', 'PRT-X-024', 20, 'each', false, '2024-02-04 13:45:00'),
('Obsolete Component Y', 'CMP-Y-025', 15, 'each', false, '2024-02-05 14:20:00'),
('Vintage Supply Z', 'SUP-Z-026', 8, 'each', false, '2024-02-06 15:55:00'),
('Old Device AA', 'DEV-AA-027', 12, 'each', false, '2024-02-07 08:10:00'),
('Legacy Accessory BB', 'ACC-BB-028', 4, 'each', false, '2024-02-08 09:25:00'),
('Discontinued Widget CC', 'WID-CC-029', 25, 'each', false, '2024-02-09 10:40:00'),
('Retired Gadget DD', 'GAD-DD-030', 3, 'each', false, '2024-02-10 12:00:00'),
('Obsolete Tool EE', 'TOL-EE-031', 18, 'each', false, '2024-02-11 14:15:00'),
('Legacy Part FF', 'PRT-FF-032', 7, 'each', false, '2024-02-12 16:30:00');

INSERT into app_user (username, password_hash, user_role, is_enabled)
VALUES
('admin', '$2a$11$jTi3EMF6OQ5hj.V76LthJOqON96Wa6beRwp/Vpap169gENhFbqfsC', 'ADMIN', true),
('regular', '$2a$11$bOFfWIol0NpD7Os8kei9lO7PvrKcwKU1n9L0rxSLBoLk7DsLEJJKO', 'USER', true);

INSERT into inventory_movement (inventory_item_id, quantity, movement_type, movement_date, reference, note, created_by)
VALUES
(1, 50, 'RECEIVE', DATE '2024-01-10', 'invoice 2', 'note for item 1', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 3', 'note for item 2', 2),
(2, 20, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 10, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 10, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 20, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 40, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(1, 5, 'SALE', DATE '2024-01-12', 'sale invoice 5', 'note for sale of item 1', 1);