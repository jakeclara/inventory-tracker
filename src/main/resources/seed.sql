INSERT into inventory_item (item_name, item_sku, reorder_threshold, item_unit, is_active, created_at)
VALUES
('Widget A', 'WID-A-001', 10, 'each', true, '2024-01-10 10:00:00'),
('Widget B', 'WID-B-002', 15, 'each', true, '2024-01-11 11:00:00'),
('Gadget C', 'GAD-C-003', 5, 'each', true, '2024-01-12 12:00:00'),
('Gadget D', 'GAD-D-004', 20, 'each', true, '2024-01-13 13:00:00'),
('Tool E', 'TOL-E-005', 8, 'each', true, '2024-01-14 14:00:00');

INSERT into app_user (username, password_hash, user_role, is_enabled)
VALUES
('admin', 'hashed_password_1', 'ADMIN', true),
('regular', 'hashed_password_2', 'USER', true);

INSERT into inventory_movement (inventory_item_id, quantity, movement_type, movement_date, reference, note, created_by)
VALUES
(1, 50, 'RECEIVE', DATE '2024-01-10', 'invoice 2', 'note for item 1', 1),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 3', 'note for item 2', 2),
(2, 30, 'RECEIVE', DATE '2024-01-11', 'invoice 4', 'note for item 2', 1),
(1, 5, 'SALE', DATE '2024-01-12', 'sale invoice 5', 'note for sale of item 1', 1);