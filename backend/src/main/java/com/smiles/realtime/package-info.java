/**
 * Real-time Communication module.
 *
 * Handles:
 * - WebSocket connections
 * - Real-time event broadcasting
 * - Calendar synchronization
 * - Notification delivery
 * - Live updates for collaborative features
 *
 * This module listens to events from other modules and
 * broadcasts them to connected clients via WebSocket.
 *
 * Listens to events from: appointments, patients, staff
 */
package com.smiles.realtime;
